name := "TweetAnalyser"
 
version := "1.0" 
      
lazy val `tweetanalyser` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(ehcache , ws , specs2 % Test , guice )
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "com.pauldijou" %% "jwt-play-json" % "2.1.0",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.mindrot" % "jbcrypt" % "0.3m"

)

libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.2.0"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

//libraryDependencies ++= Seq("com.softwaremill.sttp" %% "core" % "1.5.16")

libraryDependencies ++= List(
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.5.16",
  "com.softwaremill.sttp" %% "json4s" % "1.5.16",
  "org.json4s" %% "json4s-native" % "3.6.0",
  "com.typesafe.play" %% "play-json" % "2.7.2"
)

libraryDependencies += ws

libraryDependencies += ehcache

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))




