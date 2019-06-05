package controllers

import models.User
import play.api.mvc.{Action, RequestHeader, Result}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtJson}
import play.api.mvc.Results.Unauthorized

import scala.util.{Failure, Success, Try}


object JwtUtility {

  val secret: String = "JwtSecret"
  val auth_type: String = "Bearer"
  val algo = JwtAlgorithm.HS256

  def mustBeAuthenticated(implicit req: RequestHeader) = {
    if (!verifyJwt(req))
      Unauthorized("You must be logged in")
  }

  def verifyJwt(implicit req: RequestHeader): Boolean = {
    val jwt_token = getJwtToken(req)

    if (jwt_token.isEmpty)
      return false

    val res = Try(JwtJson.decode(jwt_token, secret, Seq(JwtAlgorithm.HS256)))

    try {
      var result = JwtJson.decode(jwt_token, secret, Seq(JwtAlgorithm.HS256))
      result.isSuccess
    } catch {
      case e: Exception => {
        println(s"Exception: ${e}")
        false
      }
    }
  }

  def authenticate(user: User): String = {
    val header = Json.obj(("typ", "JWT"), ("alg", "HS256"))
    val claim = Json.obj(("user", user.email), ("nbf", 1431520421))
    JwtJson.encode(header, claim, secret)
  }

  def getUser(token: String): String = {

    val json1: JsValue = Json.parse(JwtJson.decode(token, secret, Seq(JwtAlgorithm.HS256)).get.content)

    val res = json1("user").as[JsString].value
    res

    //val json: JsValue = Json.parse(JwtJson.decode(token, secret, Seq(JwtAlgorithm.HS256)).get.content)
    //(json \\ "user").head.toString().trim
  }

  def getJwtToken(implicit req: RequestHeader): String = {
    val header = req.headers.toMap
    if (header.contains("Authorization")) {
      header.get("Authorization") match {
        case Some(i) => return i.head.substring(i.head.indexOf(auth_type) + auth_type.length).trim
      }
    }
    ""
  }

  def getUser(implicit req: RequestHeader): String = {
    getUser(getJwtToken(req))
  }

}
