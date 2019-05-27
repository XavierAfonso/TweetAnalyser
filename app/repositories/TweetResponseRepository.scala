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

  def get(tweet_id: Long): Future[Option[TweetResponse]] = {
    db.run(tweets.filter(_.tweet_id === tweet_id).result.headOption)
  }

  def listAll: Future[Seq[TweetResponse]] = {
    db.run(tweets.result)
  }

  def insert(_tweets: Seq[TweetResponse]): Future[Unit] =
    db.run(this.tweets ++= _tweets).map(_ => ())

  def update(tweet_id: Long, tweet: TweetResponse): Future[Unit] = {
    val tweetToUpdate: TweetResponse = tweet.copy(tweet_id)
    db.run(tweets.filter(_.tweet_id === tweet_id).update(tweetToUpdate)).map(_ => ())
  }

  def delete(tweet_id: Long): Future[Unit] =
    db.run(tweets.filter(_.tweet_id === tweet_id).delete).map(_ => ())

}
