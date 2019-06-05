package controllers

import javax.inject._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import models.{Tweet, TweetResponse}
import pdi.jwt.JwtUtils
import play.api.mvc._
import play.api.libs.ws._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Results.Unauthorized
import repositories.TweetRepository
import services.TwitterClientService

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

case class HttpBinResponse(origin: String, headers: Map[String, String])

import repositories.{TweetResponseRepository,TweetRepository, UserRepository}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TwitterAnalysisController @Inject()(cc: ControllerComponents,
                                          twitterRepository: TweetRepository,
                                          tweetResponseRepository: TweetResponseRepository,
                                          ws: WSClient,
                                          twitterService: TwitterClientService) extends AbstractController(cc) {

  println(this.getClass().getName)
  val logger: Logger = Logger(this.getClass())

  implicit val tweetResponseWrites = Json.writes[TweetResponse]


  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  //twitterAccountName: String = "@lemondefr"
  def analyze(screenName: String,mode:Long) = Action.async { request =>
    logger.info("In analyze endpoint")
    JwtUtility.mustBeAuthenticated(request)


    val user = JwtUtility.getUser(request)

    twitterService.anaylze(screenName,mode,user).map(res => {
      Ok(Json.obj("response" -> "ok"))
    })
  }


  implicit val userWrites = new Writes[Tweet] {
    def writes(tweet: Tweet) = Json.obj(
      "tweet_id" -> tweet.tweet_id,
      "analyzed_at" -> tweet.analyzed_at,
      "author_screen_name" -> tweet.author_screen_name,
      "avg_sentiment" -> tweet.avg_sentiment,
      "full_text" -> tweet.full_text,
      "id" -> tweet.id,
    )
  }

  implicit val userWrites2 = new Writes[TweetResponse] {
    def writes(response: TweetResponse) = Json.obj(
      "analyzed_at" -> response.analyzed_at,
      "author_screen_name" -> response.author_screen_name,
      "full_text" -> response.full_text,
      "sentiment" -> response.sentiment,
      "fk_tweet" -> response.fk_tweet,
      "id" -> response.id
    )
  }

  def getTweets() = Action.async { request =>
    logger.info("In analyze endpoint")

    if (!JwtUtility.verifyJwt(request)) {
      Unauthorized("You must be logged in")
    }

    val user = JwtUtility.getUser(request)

    twitterRepository.listAllTweetUserId(user).map(res => {
      Ok(Json.toJson(res))
    })

  }

  def getTweetResponses(id:Long) = Action.async { request =>
    logger.info("In analyze endpoint")

    if (!JwtUtility.verifyJwt(request)) {
      Unauthorized("You must be logged in")
    }

    tweetResponseRepository.listAllTweetId(id).map(res => {
      Ok(Json.toJson(res))
    })
  }

  def deleteTweet(id:Long) = Action.async { request =>
    logger.info("In analyze endpoint")

    if (JwtUtility.verifyJwt(request)) {

      val user = JwtUtility.getUser(request)

      var continue = true

      val tweet = Try(Await.result(twitterRepository.get(id), Duration.Inf))
      tweet match {
        case scala.util.Success(res) =>
          val value: Tweet = res.asInstanceOf[Some[Tweet]].value

          if(user != value.user_id){
            continue = false
          }

      }

      if(continue) {

        twitterRepository.delete(id).flatMap(res => {
          tweetResponseRepository.deleteAllTweetId(id).map(res2 => {
            Ok(Json.toJson("ok"))
          })
        })
      }
      else{
        twitterRepository.get(id).map( res => {
          Unauthorized("You have not the right access")
        })
      }
    }
    else{

      twitterRepository.get(id).map( res => {
        Unauthorized("You must be logged in")
      })
    }
  }
}
