organization := "com.agileengine"
name := "xmlanalyzer"
version := "0.0.1"
scalaVersion := "2.12.3"

resolvers ++= Seq(Resolver.sonatypeRepo("releases"), Resolver.jcenterRepo)

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.11.2",
  "org.typelevel" %% "cats-core" % "2.0.0",
  "org.scalactic" %% "scalactic" % "3.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
mainClass in assembly := Some("com.agileengine.analyzer.SimpleXmlAnalyzer")