package controllers

import java.sql.Timestamp
import java.time.Instant

import javax.inject._
import javax.inject.Inject
import models.User
import play.api.mvc._
import play.api.Logger
import play.api.libs.json._
import repositories.{TweetRepository, UserRepository}
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userRepository: UserRepository) extends AbstractController(cc) {

  val ec = scala.concurrent.ExecutionContext.Implicits.global

  val logger: Logger = Logger(this.getClass())
  implicit val userWrites = new Writes[User] {
    def writes(user: User) = Json.obj(
      "email" -> user.email,
      "created_at" -> user.created_at
    )
  }

  def login(email: String, password: String) = Action.async { request =>
    userRepository.get(email).map {
      case None => NotFound(Json.obj("error" -> "User not found."))
      case Some(user) => {
        if (BCrypt.checkpw(password, user.password)) {
          logger.info(s"User ${user.email} correctly logged in")
          Ok(Json.obj("token" -> JwtUtility.authenticate(user)))
        } else {
          Unauthorized("Credentials doesn't match our records.")
        }
      }
    }(ec)
  }


  def register(email: String, password: String) = Action.async {
    val user = User(email, BCrypt.hashpw(password, BCrypt.gensalt(10)), Timestamp.from(Instant.now()))
    userRepository.insert(user).map( _ =>
      JwtUtility.authenticate(user))(ec).map(token => {
        logger.info(s"User ${user.email} registered logged in")
        Ok(Json.obj("token" -> token))
      }
    )(ec)
  }
}
