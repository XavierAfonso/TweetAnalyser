package models

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


case class Tweet(id: Long, author_screen_name: String, tweet_id: String, full_text: String, created_at: Timestamp, analyzed_at: Timestamp, avg_sentiment: String, user_id : String)

class TweetTableDef(tag: Tag) extends Table[Tweet](tag, "tweets") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def author_screen_name = column[String]("author_screen_name")
  def tweet_id = column[String]("tweet_id")
  def full_text = column[String]("full_text")
  def created_at = column[Timestamp]("created_at")
  def analyzed_at = column[Timestamp]("analyzed_at")
  def avg_sentiment = column[String]("avg_sentiment")
  def user_id = column[String]("user_id")

  override def * = (id, author_screen_name, tweet_id, full_text, created_at, analyzed_at, avg_sentiment,user_id) <> ((Tweet.apply _).tupled, Tweet.unapply)
}

