package com.madhu.flickr

import android.os.Bundle
import android.view._


import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import android.support.v4.app._
import android.widget._
import android.view.MenuItem.OnMenuItemClickListener

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import android.util.Log
import play.api.libs.json._
import scalaj.http.Http


// import macroid stuff

import macroid._


trait MenuHelpers {

  implicit class MenuOps(item: MenuItem)(implicit appContext:
  AppContext) {
    def onClick(fn: => (MenuItem => Boolean)) = item.setOnMenuItemClickListener(
      new OnMenuItemClickListener {
        override def onMenuItemClick(item: MenuItem) = {
          fn(item)
        }
      }
    )

    implicit def toMenuItem(item: MenuItem) = new MenuOps(item)
  }


}


class FlickrFragment extends Fragment
with Contexts[Fragment] with IdGeneration with MenuHelpers {
  val tag = "com.madhu.flickr.FlickrActivity"
  var gridLayout = slot[GridView]

  class CustomeAdapter(var list: List[String]) extends BaseAdapter {
    override def getCount: Int = list.size

    override def getItem(pos: Int) = list(pos)

    override def getItemId(pos: Int) = pos

    override def getView(pos: Int, currentView: View, parent: ViewGroup): View = {
      val textView = (if (currentView != null) currentView
      else getUi {
        w[TextView]
      }).asInstanceOf[TextView]
      val value = list(pos)
      textView.setText(value)
      textView
    }

  }


  override def onStart(): Unit = {
    //var items = List[String]()
    def fetchItems = Future {
      import com.madhu.flickr.model.FlickrModels._
      val jsonReply = Http("https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=?").asString
      /*val jsonReply = """{
        "title": "Uploads from everyone",
        "link": "https://www.flickr.com/photos/",
        "description": "",
        "modified": "2014-09-03T16:15:24Z",
        "generator": "https://www.flickr.com/",
        "items": [
        {
          "title": "DSCN6563",
          "link": "https://www.flickr.com/photos/orangeaurochs/14942594079/",
          "media": {"m":"https://farm4.staticflickr.com/3900/14942594079_fcef2a39f9_m.jpg"},
          "date_taken": "2014-07-18T15:45:50-08:00",
          "published": "2014-09-03T16:15:24Z",
           "author": "nobody@flickr.com (orangeaurochs)",
           "author_id": "34905030@N00",
           "tags": ""
        }]} """
*/
      Log.d(tag, jsonReply)
      Log.d(tag, "before parsing")
      Json.parse(jsonReply).validate[FlickrFeed].recoverTotal(e => JsError.toFlatForm(e))
      val otherItems = Json.parse(jsonReply).as[FlickrFeed].items.map(value => value.media.m)
      otherItems
    } recover {
      case e => {
        Log.d(tag, "error", e)
        List[String]()
      }
    }

    fetchItems.mapUi(items => gridLayout.get.setAdapter(new CustomeAdapter(items)))

    super.onStart()
  }

  override def onCreateView(inflator: LayoutInflater,
                            parent: ViewGroup, savedBundleInstance: Bundle): View = {

    super.onCreateView(inflator, parent, savedBundleInstance)

    val gridView = l[GridView]() <~
      Tweak {
        (view: GridView) => {
          view.setColumnWidth(120 dp)
          view.setNumColumns(GridView.AUTO_FIT)
          view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH)
        }
      } <~
      wire(gridLayout) <~ matchParent

     //fetchItems.mapUi(items => gridView.get.setAdapter(new CustomeAdapter(items)))


    getUi(gridView)

  }


}