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
case class HttpBinResponse(origin: String, headers: Map[String, String])

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {

  val oauth_consumer_key:String = ConfigFactory.load().getString("env.oauth_consumer_key")
  val oauth_token:String  = ConfigFactory.load().getString("env.oauth_token")
  val oauth_signature_method:String  = ConfigFactory.load().getString("env.oauth_signature_method")
  val oauth_timestamp:String  = ConfigFactory.load().getString("env.oauth_timestamp")
  val oauth_nonce:String  = ConfigFactory.load().getString("env.oauth_nonce")
  val oauth_version:String  = ConfigFactory.load().getString("env.oauth_version")
  val oauth_signature:String  = ConfigFactory.load().getString("env.oauth_signature")


  val headers: String = "OAuth oauth_consumer_key=" + oauth_consumer_key +",oauth_token="+oauth_token+",oauth_signature_method="+oauth_signature_method+",oauth_timestamp="+oauth_timestamp+",oauth_nonce="+oauth_nonce+",oauth_version="+oauth_version+",oauth_signature="+oauth_signature+""

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val request: WSRequest = ws.url("https://api.twitter.com/1.1/statuses/user_timeline.json?user_id=2886752038&count=1")

  val complexRequest: WSRequest =

    request.addHttpHeaders("Authorization" -> headers )
      .withRequestTimeout(10000.millis)

  val futureResponse: Future[WSResponse] = complexRequest.get()

  futureResponse onComplete {
    case Success(res) => println(res.body)
    case Failure(ex) => println(ex)
  }
}
