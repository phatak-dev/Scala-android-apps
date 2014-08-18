package com.madhu.criminalintent

import play.api.libs.json.Json
import java.io._
import android.content.Context
import scala.io.Source
import android.util.Log.d

/**
 * Created by madhu on 12/8/14.
 */
class CrimeSerializer(fileName: String, context: Context) {

  def withOutputFile(op: => OutputStreamWriter => Unit) = {
    var writer: OutputStreamWriter = null
    try {
      val out = context.openFileOutput(fileName, Context.MODE_PRIVATE)
      writer = new OutputStreamWriter(out)
      op(writer)
    }
    finally {
      if (writer != null) writer.close()
    }

  }

  def withInputFile(op: => InputStream => Unit) = {
    var in: FileInputStream = null
    try {
      in = context.openFileInput(fileName)
      op(in)
    }
    catch {
      case e:Exception => { d("$$$$$$$$","file issue",e)}
    }
    finally {
      if (in != null) in.close()
    }

  }

  def readCrimes(): List[Crime] = {
    var jsonString: String = ""
    withInputFile(
      (in: InputStream) => {
        jsonString = Source.fromInputStream(in).mkString
      }
    )

    val crimeList = if (jsonString.isEmpty) List[Crime]() else Json.parse(jsonString).as[List[Crime]]
    crimeList
  }

  def saveCrimes(crimes: List[Crime]) = {

    val json = Json.toJson(crimes)
    val jsonString = Json.stringify(json)
    withOutputFile {
      (writer: OutputStreamWriter) => {
        writer.write(jsonString)
      }
    }
  }

}
