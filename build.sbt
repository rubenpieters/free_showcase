
name := "free_showcase"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-Ypartial-unification")

resolvers += Resolver.bintrayRepo("projectseptemberinc", "maven")

val circeVersion = "0.6.1"
val monixVersion = "2.1.2"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.8.1"
  ,"org.atnos" %% "eff" % "2.2.0"
  ,"com.projectseptember" %% "freek" % "0.6.6"
  ,"io.monix" %% "monix" % monixVersion
  ,"io.monix" %% "monix-cats" % monixVersion

  // String similarity
  ,"com.github.mpkorstanje" % "simmetrics-core" % "4.1.1"

  // HTTP
  ,"org.scalaj" %% "scalaj-http" % "2.3.0"

  // JSON
  ,"io.circe" %% "circe-core" % circeVersion
  ,"io.circe" %% "circe-generic" % circeVersion
  ,"io.circe" %% "circe-parser" % circeVersion

  ,"org.scalatest" %% "scalatest" % "3.0.0" % "test"
)


addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")