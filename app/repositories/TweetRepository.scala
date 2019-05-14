package repositories

import models.{Tweet, TweetTableDef}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TweetRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val tweets = TableQuery[TweetTableDef]


  def insert(tweet: Tweet): Future[Unit] =
    db.run(tweets += tweet).map(_ => ())

  def get(tweet_id: Long): Future[Option[Tweet]] = {
    db.run(tweets.filter(_.tweet_id === tweet_id).result.headOption)
  }

  def listAll: Future[Seq[Tweet]] = {
    db.run(tweets.result)
  }

  def insert(_tweets: Seq[Tweet]): Future[Unit] =
    db.run(this.tweets ++= _tweets).map(_ => ())

  def update(tweet_id: Long, tweet: Tweet): Future[Unit] = {
    val tweetToUpdate: Tweet = tweet.copy(tweet_id)
    db.run(tweets.filter(_.tweet_id === tweet_id).update(tweetToUpdate)).map(_ => ())
  }

  def delete(tweet_id: Long): Future[Unit] =
    db.run(tweets.filter(_.tweet_id === tweet_id).delete).map(_ => ())

}
