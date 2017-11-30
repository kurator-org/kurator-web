import sbt.ExclusionRule

name := "kurator-web"

version := "1.0.2-SNAPSHOT"

fullResolvers :=  Seq(
  "Open Source Geospatial Foundation Repository" at "http://download.osgeo.org/webdav/geotools/",
  "public" at "https://repo1.maven.org/maven2/", // default repo
  Resolver.mavenLocal
)

updateOptions := updateOptions.value.withLatestSnapshots(false)

lazy val `kurator-web` = (project in file(".")).enablePlugins(PlayJava, PlayEbean, SbtWeb)
scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions
)

libraryDependencies += "com.typesafe.play" %% "play-mailer" % "6.0.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "6.0.0"

libraryDependencies ++= Seq(
  "commons-io" % "commons-io" % "2.4",
  "org.pac4j" % "play-pac4j" % "2.4.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.kurator" % "kurator-validation" % "1.0.2-SNAPSHOT" exclude("com.typesafe.akka", "akka-actor_2.10") exclude("com.typesafe.akka", "akka-slf4j_2.10"),
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "be.objectify" %% "deadbolt-java" % "2.5.4",

  "com.fasterxml.jackson.core" % "jackson-core" % "2.7.6",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.7.6",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.3"
)

libraryDependencies := libraryDependencies.value.map(_.excludeAll(
    ExclusionRule("org.springframework", "spring-context"),
    ExclusionRule("org.springframework", "spring-core"),
    ExclusionRule("org.springframework", "spring-beans"),
    ExclusionRule("org.slf4j", "slf4j-log4j12")
  )
)

libraryDependencies += "org.springframework" % "spring-context" % "3.1.2.RELEASE"

libraryDependencies += "com.github.stefanbirkner" % "system-rules" % "1.16.1" % "test"

playEbeanModels in Compile := Seq("models.*")

/* pipelineStages := Seq(rjs) */
