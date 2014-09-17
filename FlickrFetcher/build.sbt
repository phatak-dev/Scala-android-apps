import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-19"

name := "FlickrFetcher"

scalaVersion := "2.11.1"




run <<= run in Android

apkbuildExcludes in Android ++= Seq("META-INF/LICENSE","META-INF/NOTICE","META-INF/LICENSE.txt","META-INF/NOTICE.txt")

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "spray" at "http://repo.spray.io/"
)

// scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")



libraryDependencies ++= Seq(
  "org.macroid" %% "macroid" % "2.0.0-M3",
  compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10"),
  "com.typesafe.play" %% "play-json" % "2.3.3",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.scalaj" %% "scalaj-http" % "0.3.16",
  "com.android.support" % "support-v4" % "18.0.+"
)

proguardScala in Android := true


proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic",
  "-keep class scala.concurrent.ExecutionContext",
  "-keep public class play.api.libs.json.** {*;}",
  "-keep class com.fasterxml.jackson.databind.** { *; }",
  "-keep interface com.fasterxml.jackson.databind.** { *; }"
)
