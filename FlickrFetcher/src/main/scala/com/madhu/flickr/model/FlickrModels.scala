package com.madhu.flickr.model

import play.api.libs.json.Json


/**
 * Created by madhu on 3/9/14.
 */
object FlickrModels {

  case class Photo(m:String)
  case class Item(title:String,link:String,date_taken:String,media:Photo)
  case class FlickrFeed(title:String,link:String,description:String,
                         modified:String,generator:String,items:List[Item])//Array[Item])

  val hashCodeValue = Set(1,2,3).hashCode

  implicit val photoWriters = Json.writes[Photo]
  implicit val photoReaders = Json.reads[Photo]

  implicit val itemWriters = Json.writes[Item]
  implicit val itemReaders = Json.reads[Item]

  implicit val feedWriters = Json.writes[FlickrFeed]
  implicit val feedReaders = Json.reads[FlickrFeed]
}
