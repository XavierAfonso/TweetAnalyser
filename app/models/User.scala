package models

import java.sql.Timestamp

import play.api.libs.json._
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

case class User(email: String, password: String, created_at: Timestamp)

class UserTableDef(tag: Tag) extends Table[User](tag, "users") {

  def email = column[String]("email", O.PrimaryKey)
  def password = column[String]("password")
  def created_at = column[Timestamp]("created_at")

  override def * = (email, password, created_at) <> ((User.apply _).tupled, User.unapply)


}

