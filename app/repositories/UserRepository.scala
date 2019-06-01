package repositories

import javax.inject.{Inject, Singleton}
import models.{User, UserTableDef}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val users = TableQuery[UserTableDef]

  def insert(user: User): Future[Unit] =
    db.run(users += user).map(_ => ())

  def get(email: String): Future[Option[User]] = {
    println(s"Email provided ${email}")
    db.run(users.filter(_.email.trim.toLowerCase === email.trim.toLowerCase()).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  def insert(_users: Seq[User]): Future[Unit] =
    db.run(this.users ++= _users).map(_ => ())

  def update(email: String, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(email)
    db.run(users.filter(_.email === email).update(userToUpdate)).map(_ => ())
  }

  def delete(email: String): Future[Unit] =
    db.run(users.filter(_.email === email).delete).map(_ => ())

}