package repositories

import javax.inject.{Inject, Singleton}
import models.{TweetResponse, TweetResponseTableDef}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TweetResponseRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                       (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val tweets = TableQuery[TweetResponseTableDef]


  def insert(tweet: TweetResponse): Future[Unit] =
    db.run(tweets += tweet).map(_ => ())

  def get(tweet_id: String): Future[Option[TweetResponse]] = {
    db.run(tweets.filter(_.tweet_id === tweet_id).result.headOption)
  }

  def listAll: Future[Seq[TweetResponse]] = {
    db.run(tweets.result)
  }

  def listAllTweetId(id: Long): Future[Seq[TweetResponse]] = {
    db.run(tweets.filter(_.fk_tweet === id).result)
  }

  def insert(_tweets: Seq[TweetResponse]): Future[Unit] =
    db.run(this.tweets ++= _tweets).map(_ => ())

  def update(tweet_id: String, tweet: TweetResponse): Future[Unit] = {
    val tweetToUpdate: TweetResponse = tweet.copy(tweet_id.toLong)
    db.run(tweets.filter(_.tweet_id === tweet_id).update(tweetToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(tweets.filter(_.id === id).delete).map(_ => ())

  def deleteAllTweetId(id: Long): Future[Unit] =
    db.run(tweets.filter(_.fk_tweet === id).delete).map(_ => ())

}
