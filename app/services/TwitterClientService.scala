package services

import java.sql.Timestamp
import java.util.Date

import play.api.libs.ws.WSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import scala.concurrent.duration._
import play.api.libs.ws._
import com.typesafe.config.ConfigFactory
import play.api.Logger
import play.api.libs.json._
import repositories._
import models._


import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

class TwitterClientService @Inject() (ws: WSClient,
                                      twitterRepository: TweetRepository,
                                      twitterResponseRepository: TweetResponseRepository) {

  val logger: Logger = Logger(this.getClass)
  val token: String  = ConfigFactory.load().getString("env.token")
  val headers: String = "Bearer " + token

  def analyseSentiment(input : String):Int = {
    val sentiment = SentimentAnalyzer.mainSentiment(input)
    sentiment match {
      case Sentiment.VERY_NEGATIVE => 0
      case Sentiment.NEGATIVE => 1
      case Sentiment.NEUTRAL => 2
      case Sentiment.POSITIVE => 3
      case Sentiment.VERY_POSITIVE => 4
    }
  }

  // Get the tweets
  def getTweets (n : Int, twitterAccountName: String):List[JsObject]= {

    val prom = Promise[List[JsObject]]()

    def loop(cpt:Int,request:String, acc:List[JsObject]):Future[List[JsObject]]={

      cpt match {

        case 0 => prom.success(acc).future
        case _ => {

          val requestSearchTweets: WSRequest = ws.url("https://api.twitter.com/1.1/search/tweets.json"+request+"&tweet_mode=extended")

          val complexRequestSearchTweets: WSRequest =
            requestSearchTweets.addHttpHeaders("Authorization" -> headers )
              .withRequestTimeout(10000.millis)

          complexRequestSearchTweets.get().map( res => {

            val tweets: String = res.body
            val searchReponse: JsObject = Json.parse(tweets).as[JsObject]
            val tweetsUser: JsObject = searchReponse("search_metadata").as[JsObject]
            var nextResults: String = ""

            if ((tweetsUser \ "next_results").isDefined) {
              nextResults = tweetsUser("next_results").as[JsString].value
            }

            //No more next, need to stop here
            if (nextResults=="") {
              return loop(0, "", acc)
            } else {
              return loop(cpt - 1, nextResults, searchReponse :: acc)
            }
          })
        }
      }
    }
    
    val request : String = "?q="+twitterAccountName+"&count=100&result_type=recent&tweet_mode=extended"
    loop(n,request,Nil)
    Await.result(prom.future, Duration.Inf)
   // prom.future
  }

  def anaylze(twitterAccountName: String) = {
    val token: String  = ConfigFactory.load().getString("env.token")
    val headers: String = "Bearer " + token

    logger.debug(s"Authentication with Bearer token: $headers")

    ws.url("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + twitterAccountName + "&count=500&exclude_replies=true&include_rts=false")
      .addHttpHeaders("Authorization" -> headers )
      .get()
      .map {
        response => {
          val lastTweet:String = response.body

          logger.debug(s"Last tweet: $lastTweet")

          // Get the id of the last tweet
          val userTimeline: List[JsValue] = Json.parse(lastTweet).as[List[JsValue]]
          val tweet: JsValue = userTimeline(0)
          val id: String = tweet("id_str").as[JsString].value

          // Search tweets
          val requestSearchTweets: WSRequest = ws.url("https://api.twitter.com/1.1/search/tweets.json?q="+twitterAccountName+"&count=500&result_type=recent&tweet_mode=extended&include_rts=false")

          val nbPage : Int = 10
          val data: List[JsObject] = getTweets(nbPage,twitterAccountName)

          // Save the original tweet
          val original_tweet: Tweet = Tweet(id.toLong, id.toLong, tweet("text").as[JsString].value,
          new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime),
          new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime), "Neutral")

          logger.debug(s"Tweet object: $original_tweet")

          val tweet_creation = Try(Await.result(twitterRepository.insert(original_tweet), Duration.Inf))
          tweet_creation match {
            case scala.util.Success(value) =>
              logger.debug(s"Correctly saved the tweet with id: ${original_tweet.tweet_id}")
          }

          twitterRepository
          .listAll
          .foreach(x => logger.debug("GOT tweet: " + x.toString() + "\n"))

          data.flatMap(
            related_tweets => {
              val tweets: String = related_tweets.toString()
              val searchReponse: JsObject = Json.parse(tweets).as[JsObject]
              val tweetsUser: List[JsValue] = searchReponse("statuses").as[List[JsValue]]

              // Keep only tweets with "in_reply_to_status_id_str"
              val tweetsUserFiltred = tweetsUser.filter(x => (x \ "in_reply_to_status_id_str").asOpt[String].isDefined)
              logger.debug("tweetsUserFiltred: " + tweetsUserFiltred)

              // Get all the replies of the specified tweet
              tweetsUserFiltred.map(x => logger.debug(x("in_reply_to_status_id_str").as[JsString] + "is same as " + id))

              val replies = tweetsUserFiltred
                .filter(x => x("in_reply_to_status_id_str").as[JsString].value == id)

              val mapped_replies = replies.map(x => x("full_text").as[JsString].value)
                .map(x => x.split("\\s+").filterNot(_.head == '@').mkString(" "))
                .filter(_.isEmpty == false)

              /**
                * Analyze sentiment of each response and persist them
                */
              for (reply <- replies; if (reply \ "full_text").isDefined) {
                // Analyze each response of the tweet and store it
                val tweet_text = reply("full_text").as[JsString].value.toString.replaceAll("[^\u0000-\uFFFF]", "")
                  .split("\\s+").filterNot(_.head == '@').mkString(" ")


                if (!tweet_text.isEmpty) {

                val idReponse: String = reply("id_str").as[JsString].value

                val sentiment = analyseSentiment(tweet_text)

                // Persist the tweet response
                val tweet_response: TweetResponse = models.TweetResponse(idReponse.toLong, idReponse.toLong, tweet_text,
                  new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime),
                  new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime), sentiment, original_tweet.tweet_id)

                val tweet_response_creation = Try(Await.result(twitterResponseRepository.insert(tweet_response), Duration.Inf))
                tweet_response_creation match {
                  case scala.util.Success(value) =>
                    logger.debug(s"Correctly saved the tweet with id: ${original_tweet.tweet_id}")
                }
                logger.debug(s"Found one reply: $reply")

              }
            }

              logger.debug(s"Tweet: $tweet")
              logger.debug(s"Replies: $replies")

              mapped_replies
            }
          )
        }
      }
  }
}
