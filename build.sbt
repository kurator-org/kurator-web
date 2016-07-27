import sbt.ExclusionRule

name := "play-java"

version := "1.0-SNAPSHOT"

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
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.kurator" % "kurator-akka" % "0.4-SNAPSHOT" exclude("com.typesafe.akka", "akka-actor_2.10") exclude("com.typesafe.akka", "akka-slf4j_2.10") ,
  "org.kurator" % "kurator-validation" % "0.4-SNAPSHOT" ,
  "org.kurator" % "kurator-fp-validation" % "0.4-SNAPSHOT"
)

libraryDependencies := libraryDependencies.value.map(_.excludeAll(
    ExclusionRule("org.springframework", "spring-context"),
    ExclusionRule("org.springframework", "spring-core"),
    ExclusionRule("org.springframework", "spring-beans")
  )
)

libraryDependencies += "org.springframework" % "spring-context" % "3.1.2.RELEASE"

playEbeanModels in Compile := Seq("models.*")