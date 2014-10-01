import com.typesafe.sbt.SbtStartScript

organization  := "com.articlio"

version       := "0.1"

//
// akka & spray
//

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.7" % "test"
  )
}

seq(SbtStartScript.startScriptForClassesSettings: _*)

//
// spray revolver, only for development
//

Revolver.settings 

mainClass in Revolver.reStart := Some("org.vertx.java.platform.impl.cli.Starter")

Revolver.reStartArgs := Seq("run", "scala:com.articlio.ldb.deployer")

//
// Vertx
//

// vertx-scala doesn't ship on 2.9
// scalaVersion := "2.10.2"

// Fork required to avoid conflicts when compiling the .scala source on the fly
// fork := true

libraryDependencies ++= Seq(
  // If changing lang-scala version, make sure src/main/resources/langs.properties is updated too
  "io.vertx" % "lang-scala" % "1.0.0",
  "io.vertx" % "vertx-platform" % "2.1M1"
)

// spray-json
libraryDependencies += "io.spray" %%  "spray-json" % "1.2.6"

libraryDependencies += "org.ahocorasick" % "ahocorasick" % "0.2.3"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "latest.release"

resolvers += "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "com.github.verbalexpressions" %% "scalaverbalexpression" % "1.0.1"