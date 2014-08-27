import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-19"

name := "NerdLauncher"

scalaVersion := "2.11.1"




run <<= run in Android



resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

// scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")


libraryDependencies ++= Seq(
  "org.macroid" %% "macroid" % "2.0.0-M3",
  compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10"),
  "com.android.support" % "support-v4" % "18.0.+"
)

proguardScala in Android := true

proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic",
  "-keep class scala.concurrent.ExecutionContext",
  "-keep class scala.Option",
  "-keep class com.madhu.quiz.R"
)
