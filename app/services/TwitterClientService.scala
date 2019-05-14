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

import scala.concurrent.Await
import scala.util.Try

class TwitterClientService @Inject() (ws: WSClient,
                                      twitterRepository: TweetRepository) {

  val logger: Logger = Logger(this.getClass)

  def anaylze(twitterAccountName: String) = {
    val token: String  = ConfigFactory.load().getString("env.token")
    val headers: String = "Bearer " + token

    logger.debug(s"Authentication with Bearer token: $headers")

    ws.url("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + twitterAccountName + "&count=500&exclude_replies=true&include_rts=false")
      .addHttpHeaders("Authorization" -> headers )
      .get()
      .flatMap {
        response => {
          val lastTweet:String = response.body

          logger.debug(s"Last tweet: $lastTweet")

          // Get the id of the last tweet
          val userTimeline: List[JsValue] = Json.parse(lastTweet).as[List[JsValue]]
          val tweet: JsValue = userTimeline(0)
          val id: String = tweet("id_str").as[JsString].value

          // Search tweets
          val requestSearchTweets: WSRequest = ws.url("https://api.twitter.com/1.1/search/tweets.json?q="+twitterAccountName+"&count=500&result_type=recent&tweet_mode=extended&include_rts=false")

          val complexRequestSearchTweets: WSRequest =
            requestSearchTweets.addHttpHeaders("Authorization" -> headers )
              .withRequestTimeout(10000.millis)

          complexRequestSearchTweets.get().map(
            related_tweets => {
              val tweets: String = related_tweets.body
              val searchReponse: JsObject= Json.parse(tweets).as[JsObject]
              val tweetsUser: List[JsValue] = searchReponse("statuses").as[List[JsValue]]

              // Keep only tweets with "in_reply_to_status_id_str"
              val tweetsUserFiltred = tweetsUser.filter(x => (x \ "in_reply_to_status_id_str").asOpt[String].isDefined)
              logger.debug("tweetsUserFiltred: " + tweetsUserFiltred)

              // Get all the replies of the specified tweet
              tweetsUserFiltred.map(x => logger.debug(x("in_reply_to_status_id_str").as[JsString] + "is same as " + id))

              val replies = tweetsUserFiltred
                .filter(x => x("in_reply_to_status_id_str").as[JsString].value==id)
                .map(x => x("full_text").as[JsString].value )

              logger.debug(s"Tweet: $tweet")
              logger.debug(s"Replies: $replies")

              // Save the original tweet
              val original_tweet: Tweet = Tweet(id.toLong, id.toLong, tweet("text").as[JsString].value,
                new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime),
                new java.sql.Timestamp(new java.util.Date(tweet("created_at").as[JsString].value).getTime))
              logger.debug(s"Tweet object: $original_tweet")

              val tweet_creation = Try(Await.result(twitterRepository.insert(original_tweet), Duration.Inf))
              tweet_creation match {
                case scala.util.Success(value) =>
                  logger.debug(s"Correctly saved the tweet with id: ${original_tweet.tweet_id}")
              }

              twitterRepository
                .listAll
                .foreach(x => logger.debug("GOT tweet: " + x.toString() + "\n"))

              replies
            }
          )
        }
      }
  }

}
