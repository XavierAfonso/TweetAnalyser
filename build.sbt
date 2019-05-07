name := "TweetAnalyser"
 
version := "1.0" 
      
lazy val `tweetanalyser` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

//libraryDependencies ++= Seq("com.softwaremill.sttp" %% "core" % "1.5.16")

libraryDependencies ++= List(
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.5.16",
  "com.softwaremill.sttp" %% "json4s" % "1.5.16",
  "org.json4s" %% "json4s-native" % "3.6.0"
)

libraryDependencies += ws

libraryDependencies += ehcache




