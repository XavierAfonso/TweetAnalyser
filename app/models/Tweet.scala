package models

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class Tweet(tweet_id: Long, author_id: Long, full_text: String, created_at: Timestamp, analyzed_at: Timestamp, avg_sentiment: String)

class TweetTableDef(tag: Tag) extends Table[Tweet](tag, "tweets") {

  def tweet_id = column[Long]("tweet_id", O.PrimaryKey,O.AutoInc)
  def author_id = column[Long]("author_id")
  def full_text = column[String]("full_text")
  def created_at = column[Timestamp]("created_at")
  def analyzed_at = column[Timestamp]("analyzed_at")
  def avg_sentiment = column[String]("avg_sentiment")

  override def * = (tweet_id, author_id, full_text, created_at, analyzed_at, avg_sentiment) <> ((Tweet.apply _).tupled, Tweet.unapply)
}

