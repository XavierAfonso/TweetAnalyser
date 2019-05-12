package controllers

import javax.inject._
import play.api.libs.ws.WSRequest

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.mvc._
import play.api.libs.ws._

import scala.util.{Failure, Success}
import com.typesafe.config.ConfigFactory
import play.api.libs.json._
case class HttpBinResponse(origin: String, headers: Map[String, String])

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val token:String  = ConfigFactory.load().getString("env.token")
  val headers: String = "Bearer " + token

  //val screenName:String = "@Chronique_NEXUS"
  //val screenName:String = "@FrancoisTheurel"

  val screenName:String = "@lemondefr"

  //Last tweets on the user_timeline
  val requestTimeline: WSRequest = ws.url("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name="+screenName+"&count=1&exclude_replies=true")

  //Request userTimeline
  val complexRequestTimeline: WSRequest =
    requestTimeline.addHttpHeaders("Authorization" -> headers )
    .withRequestTimeout(10000.millis)

  val futureResponseTimeLine: Future[WSResponse] = complexRequestTimeline.get()

  futureResponseTimeLine onComplete {
    case Success(res) => {

      val lastTweet:String = res.body

      //Get the id of the last tweet
      val userTimeline: List[JsValue] = Json.parse(lastTweet).as[List[JsValue]]
      val tweet:JsValue = userTimeline(0)
      val id:String = tweet("id_str").as[JsString].value

      //Search tweets
      val requestSearchTweets: WSRequest = ws.url("https://api.twitter.com/1.1/search/tweets.json?q="+screenName+"&count=100&result_type=mixed")

      val complexRequestSearchTweets: WSRequest =

        requestSearchTweets.addHttpHeaders("Authorization" -> headers )
        .withRequestTimeout(10000.millis)

      val futureResponseSearchTweets: Future[WSResponse] = complexRequestSearchTweets.get()

      futureResponseSearchTweets onComplete {
        case Success(res) => {

          val tweets:String = res.body
          val searchReponse:JsObject= Json.parse(tweets).as[JsObject]
          val tweetsUser:List[JsValue] = searchReponse("statuses").as[List[JsValue]]

          //Keep only tweets with "in_reply_to_status_id_str"
          val tweetsUserFiltred = tweetsUser.filter(x => (x \ "in_reply_to_status_id_str").asOpt[String].isDefined)

          //print(tweetsUserFiltred)

          //Get all the replies of the specified tweet
          val replies = tweetsUserFiltred.filter(x => x("in_reply_to_status_id_str").as[JsString].value==id)
            .map(x => x("text").as[JsString].value )

          print(replies)

        }
        case Failure(ex) => println(ex)
      }
    }
    case Failure(ex) => println(ex)
  }
}
