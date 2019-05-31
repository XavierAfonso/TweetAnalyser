package controllers

import models.User
import play.api.mvc.RequestHeader
import play.api.libs.json.{JsObject, JsValue, Json}
import pdi.jwt.{JwtAlgorithm, JwtJson}

object JwtUtility {

  val secret: String = "JwtSecret"
  val auth_type: String = "Bearer"
  val algo = JwtAlgorithm.HS256

  def verifyJwt(implicit req: RequestHeader): Boolean = {
    val jwt_token = getJwtToken(req)
    !jwt_token.isEmpty
  }

  def authenticate(user: User): String = {
    val header = Json.obj(("typ", "JWT"), ("alg", "HS256"))
    val claim = Json.obj(("user", user.email), ("nbf", 1431520421))
    JwtJson.encode(header, claim, secret)
  }

  def getUser(token: String): String = {
    val json: JsValue = Json.parse(JwtJson.decode(token, secret, Seq(JwtAlgorithm.HS256)).get.content)
    (json \\ "user").head.toString().trim
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
