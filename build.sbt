name := "ark-scala"

version := "0.1"

scalaVersion := "2.12.4"

val http4sVersion = "0.18.0"
val scryptoVersion = "2.0.0"
// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % "0.9.1",
  "io.circe" %% "circe-literal" % "0.9.1",
  "org.scorexfoundation" %% "scrypto" % scryptoVersion,
  "com.madgag.spongycastle" % "core" % "1.56.0.0",
  "org.bitcoinj" % "bitcoinj-core" % "0.14.5",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
