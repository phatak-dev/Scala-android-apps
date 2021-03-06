package com.madhu.criminalintent

import java.util.{Date, UUID}
import play.api.libs.json.Json

/**
 * Created by madhu on 12/8/14.
 * Model class to hold the crime info
 */
case class Crime(var uuid: UUID = UUID.randomUUID(),
                 var mTitle: String = "",
                 var mDate: Date = new Date(), var solved: Boolean = false,
                  var imageFileName:String = "",var suspect:String="")

object Crime{
  //implicit reader and writers for the play-json
  implicit val crimeWriters = Json.writes[Crime]
  implicit val crimeReads = Json.reads[Crime]
  def getCrimeReport(mCrime:Crime):String = {
    val crimeSolved = if(mCrime.solved) "solved" else "not solved"
    s"The crime is discovered on ${mCrime.mDate} . it is $crimeSolved "
  }
}
