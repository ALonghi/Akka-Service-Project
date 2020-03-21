name := "NowTVWatchlist"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= {
    val akkaVersion = "2.6.3"
    val akkaHttpVersion = "10.1.11"
    val scalaTestVersion = "3.0.5"

    Seq(
        // akka streams
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        // akka http
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

        // testing
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
        //mongodb
        "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
        //test libraries
        "org.scalatest" %% "scalatest" % scalaTestVersion,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion,

        // Logging
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3",

    )
}