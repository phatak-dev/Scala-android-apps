import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-19"

name := "CriminalIntnet"

scalaVersion := "2.11.1"




run <<= run in Android

apkbuildExcludes in Android ++= Seq("META-INF/LICENSE","META-INF/NOTICE","META-INF/LICENSE.txt","META-INF/NOTICE.txt")




resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com" ,
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)



//scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")


libraryDependencies ++= Seq(
  "org.macroid" %% "macroid" % "2.0.0-M3",
  "com.typesafe.play" %% "play-json" % "2.3.3",
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
