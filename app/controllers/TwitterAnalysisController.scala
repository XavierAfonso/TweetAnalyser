package controllers

import javax.inject._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import pdi.jwt.JwtUtils
import play.api.mvc._
import play.api.libs.ws._
import play.api.Logger
import play.api.libs.json._
import repositories.TweetRepository
import services.TwitterClientService

case class HttpBinResponse(origin: String, headers: Map[String, String])


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TwitterAnalysisController @Inject()(cc: ControllerComponents,
                                          twitterRepository: TweetRepository,
                                          ws: WSClient,
                                          twitterService: TwitterClientService) extends AbstractController(cc) {

  println(this.getClass().getName)
  val logger: Logger = Logger(this.getClass())

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def analyze(twitterAccountName: String = "@lemondefr") = Action.async { request =>
    logger.info("In analyze endpoint")
    JwtUtility.mustBeAuthenticated(request)
    twitterService.anaylze(twitterAccountName).map(res => {
      Ok(Json.toJson(res))
    })
  }

}
