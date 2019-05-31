package models

import java.sql.Timestamp

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._


case class Tweet(id: Long, author_screen_name: String, author_id: Long, full_text: String, created_at: Timestamp, analyzed_at: Timestamp, avg_sentiment: String)

class TweetTableDef(tag: Tag) extends Table[Tweet](tag, "tweets") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def author_screen_name = column[String]("author_screen_name")
  def tweet_id = column[Long]("tweet_id")
  def full_text = column[String]("full_text")
  def created_at = column[Timestamp]("created_at")
  def analyzed_at = column[Timestamp]("analyzed_at")
  def avg_sentiment = column[String]("avg_sentiment")

  override def * = (id, author_screen_name, tweet_id, full_text, created_at, analyzed_at, avg_sentiment) <> ((Tweet.apply _).tupled, Tweet.unapply)
}

