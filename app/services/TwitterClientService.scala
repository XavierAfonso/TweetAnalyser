package services

import play.api.libs.ws.WSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import scala.concurrent.duration._
import play.api.libs.ws._
import com.typesafe.config.ConfigFactory
import play.api.Logger
import play.api.libs.json._

class TwitterClientService @Inject() (ws: WSClient) {

  val logger: Logger = Logger(this.getClass)

  def anaylze(twitterAccountName: String) = {
    val token: String  = ConfigFactory.load().getString("env.token")
    val headers: String = "Bearer " + token

    logger.debug(s"Authentication with Bearer token: $headers")

    ws.url("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + twitterAccountName + "&count=100&exclude_replies=true")
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
          val requestSearchTweets: WSRequest = ws.url("https://api.twitter.com/1.1/search/tweets.json?q="+twitterAccountName+"&count=100&result_type=recent&tweet_mode=extended")

          val complexRequestSearchTweets: WSRequest =

            requestSearchTweets.addHttpHeaders("Authorization" -> headers )
              .withRequestTimeout(10000.millis)

          complexRequestSearchTweets.get().map(
            response2 => {
              val tweets: String = response2.body
              val searchReponse: JsObject= Json.parse(tweets).as[JsObject]
              val tweetsUser: List[JsValue] = searchReponse("statuses").as[List[JsValue]]

              // Keep only tweets with "in_reply_to_status_id_str"
              val tweetsUserFiltred = tweetsUser.filter(x => (x \ "in_reply_to_status_id_str").asOpt[String].isDefined)

              logger.debug("tweetsUserFiltred: " + tweetsUserFiltred)
              //Get all the replies of the specified tweet
              tweetsUserFiltred.map(x => logger.debug(x("in_reply_to_status_id_str").as[JsString] + "is same as " + id))

              val replies = tweetsUserFiltred.filter(x => x("in_reply_to_status_id_str").as[JsString].value==id)
                .map(x => x("full_text").as[JsString].value )
              logger.debug(s"Replies: $replies")
              replies
            }
          )
        }
      }
  }

}
