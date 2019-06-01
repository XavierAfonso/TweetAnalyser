package models

import java.sql.Timestamp

import play.api.libs.json.{Json, Writes}
import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


case class TweetResponse(id: Long, author_screen_name: String, author_id: Long, full_text: String, created_at: Timestamp, analyzed_at: Timestamp, sentiment: Int, fk_tweet: Long)

class TweetResponseTableDef(tag: Tag) extends Table[TweetResponse](tag, "tweet_responses") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def author_screen_name = column[String]("author_screen_name")
  def tweet_id = column[Long]("tweet_id")
  def full_text = column[String]("full_text")
  def created_at = column[Timestamp]("created_at")
  def analyzed_at = column[Timestamp]("analyzed_at")
  def sentiment = column[Int]("sentiment")
  def fk_tweet = column[Long]("fk_tweet")

  override def * = (id, author_screen_name, tweet_id, full_text, created_at, analyzed_at, sentiment, fk_tweet) <> ((TweetResponse.apply _).tupled, TweetResponse.unapply)

}
