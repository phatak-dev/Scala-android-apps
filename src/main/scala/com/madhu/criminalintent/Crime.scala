package com.madhu.criminalintent

import java.util.{Date, UUID}
import play.api.libs.json.Json

/**
 * Created by madhu on 12/8/14.
 */
case class Crime(var uuid: UUID = UUID.randomUUID(),
                 var mTitle: String = "",
                 var mDate: Date = new Date(), var solved: Boolean = false)
object Crime{
  implicit val crimeWriters = Json.writes[Crime]
  implicit val crimeReads = Json.reads[Crime]
}
