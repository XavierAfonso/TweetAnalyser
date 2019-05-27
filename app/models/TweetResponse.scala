package models

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


case class TweetResponse(tweet_id: Long, author_id: Long, full_text: String, created_at: Timestamp, analyzed_at: Timestamp, avg_sentiment: String, fk_tweet: Long)

class TweetResponseTableDef(tag: Tag) extends Table[TweetResponse](tag, "tweet_responses") {

  def tweet_id = column[Long]("tweet_id", O.PrimaryKey,O.AutoInc)
  def author_id = column[Long]("author_id")
  def full_text = column[String]("full_text")
  def created_at = column[Timestamp]("created_at")
  def analyzed_at = column[Timestamp]("analyzed_at")
  def avg_sentiment = column[String]("avg_sentiment")
  def fk_tweet = column[Long]("fk_tweet")

  override def * = (tweet_id, author_id, full_text, created_at, analyzed_at, avg_sentiment, fk_tweet) <> ((TweetResponse.apply _).tupled, TweetResponse.unapply)

}
