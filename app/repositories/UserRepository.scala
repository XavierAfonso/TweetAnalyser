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

  def get(user_id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === user_id).result.headOption)
  }

  def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }

  def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  def insert(_users: Seq[User]): Future[Unit] =
    db.run(this.users ++= _users).map(_ => ())

  def update(user_id: Long, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(user_id)
    db.run(users.filter(_.id === user_id).update(userToUpdate)).map(_ => ())
  }

  def delete(user_id: Long): Future[Unit] =
    db.run(users.filter(_.id === user_id).delete).map(_ => ())

}
