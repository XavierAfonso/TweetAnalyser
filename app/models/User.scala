package models

import java.sql.Timestamp

import slick.lifted.Tag
import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class User(id: Long, email: String, password: String, created_at: Timestamp)

class UserTableDef(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def email = column[String]("email")
  def password = column[String]("password")
  def created_at = column[Timestamp]("created_at")

  override def * = (id, email, password, created_at) <> ((User.apply _).tupled, User.unapply)
}

