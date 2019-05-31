package controllers

import models.User
import pdi.jwt.JwtAlgorithm
import play.api.libs.json.JsObject
import play.api.mvc.RequestHeader
import play.mvc.Http.Request
import play.api.libs.json.Json
import pdi.jwt.{JwtJson, JwtAlgorithm}

object JwtUtility {

  val secret: String = "JwtSecret"
  val auth_type: String = "Bearer"
  val algo = JwtAlgorithm.HS256

  def verifyJwt(implicit req: RequestHeader): Boolean = {
    val header = req.headers.toMap
    if (header.contains("Authorization")) {
      val auth_header = header.get("Authorization").toString
      val jwt_token = auth_header.substring(auth_header.indexOf(auth_type) + auth_type.length).trim
      println(s"Token: ${jwt_token}")
    }
    false
  }

  def authenticate(user: User): String = {
    val claim = Json.obj(("user", user.id), ("nbf", 1431520421))
    JwtJson.encode(claim, secret, algo)
  }

}
