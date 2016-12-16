import sbt.ExclusionRule

name := "kurator-web"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
scalaVersion := "2.11.7"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions
)

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play-mailer_2.11" % "3.0.1",
  "commons-io" % "commons-io" % "2.4",
  "org.pac4j" % "play-pac4j" % "2.4.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.kurator" % "kurator-akka" % "1.0.0" exclude("com.typesafe.akka", "akka-actor_2.10") exclude("com.typesafe.akka", "akka-slf4j_2.10") ,
  "org.kurator" % "kurator-validation" % "1.0.0",
  "org.datakurator" % "kurator-ffdq" % "1.0.1-SNAPSHOT",
  "mysql" % "mysql-connector-java" % "5.1.18"
)

libraryDependencies := libraryDependencies.value.map(_.excludeAll(
    ExclusionRule("org.springframework", "spring-context"),
    ExclusionRule("org.springframework", "spring-core"),
    ExclusionRule("org.springframework", "spring-beans"),
    ExclusionRule("org.slf4j", "slf4j-log4j12")
  )
)

libraryDependencies += "org.springframework" % "spring-context" % "3.1.2.RELEASE"

playEbeanModels in Compile := Seq("models.*")