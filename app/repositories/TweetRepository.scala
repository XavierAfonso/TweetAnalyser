package repositories

import models.{Tweet, TweetTableDef}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class TweetRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val tweets = TableQuery[TweetTableDef]


  def insert(tweet: Tweet): Future[Unit] =
    db.run(tweets += tweet).map(_ => ())

  def get(id: Long): Future[Option[Tweet]] = {
    db.run(tweets.filter(_.id === id).result.headOption)
  }

  def getLast: Future[Option[Tweet]] = {
    db.run(tweets.sortBy(_.id.desc).result.headOption)
  }

  def listAll: Future[Seq[Tweet]] = {
    db.run(tweets.result)
  }

  def listAllTweetUserId(user_id:String): Future[Seq[Tweet]] = {
    db.run(tweets.filter(_.user_id === user_id).sortBy(_.id.desc).result)
  }

  def insert(_tweets: Seq[Tweet]): Future[Unit] =
    db.run(this.tweets ++= _tweets).map(_ => ())

  def update(tweet_id: String, tweet: Tweet): Future[Unit] = {
    db.run(tweets.filter(_.tweet_id === tweet_id).update(tweet)).map(_ => ())
  }

  def updateSentiment(tweet: Tweet) = {
    println(s"Received tweet ==> ${tweet}")
    db.run(tweets.update(tweet))
  }



  def delete(id: Long): Future[Unit] =
    db.run(tweets.filter(_.id === id).delete).map(_ => ())

}
