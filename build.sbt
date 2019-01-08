name := "SparkTaskMetrics"

version := "0.1"

scalaVersion := "2.11.8"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

isSnapshot := true

val sparkVersion = "2.4.0"

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)