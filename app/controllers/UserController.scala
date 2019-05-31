package controllers

import javax.inject._

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import pdi.jwt.JwtUtils
import play.api.mvc._
import play.api.libs.ws._
import play.api.Logger
import play.api.libs.json._
import repositories.{TweetRepository, UserRepository}
import services.TwitterClientService
import controllers.JwtUtility
import org.mindrot.jbcrypt.BCrypt



/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userRepository: UserRepository) extends AbstractController(cc) {

  println(this.getClass().getName)
  val logger: Logger = Logger(this.getClass())


  def login(email: String, password: String) = Action { request =>
    println("test")
    userRepository.findByEmail(email).map {
      case None => NotFound(Json.obj("error" -> "Not Found")))
      case Some(user) => {
        //Ok(Json.obj("result" -> thing)))
        BCrypt.checkpw(password, user.password)
      }
    }

    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt)
    user.value
    val res = JwtUtility.verifyJwt(request)
    Ok(s"ok => ${res}")
  }

  def me() = Action { request =>
    Ok("ok")
  }

}
