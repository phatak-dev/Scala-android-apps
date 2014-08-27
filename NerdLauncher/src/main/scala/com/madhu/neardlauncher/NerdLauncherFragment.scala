package com.madhu.neardlauncher

import android.os.Bundle
import android.view.ViewGroup
import android.view.View










import macroid.FullDsl._
import android.content.Intent
import android.support.v4.app._
import android.view.LayoutInflater
import android.util.Log
import scala.collection.JavaConverters._
import android.widget.{ListView, TextView, BaseAdapter, ArrayAdapter}
import android.content.pm.ResolveInfo

// import macroid stuff
import macroid._
class NerdLauncherFragment extends ListFragment
  with Contexts[ListFragment] {
  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    
  }

  class CustomerAdpater(val activities:List[ResolveInfo]) extends BaseAdapter{
    override def getCount: Int = activities.size
    override def getView(position: Int,
                         convertView: View, parent: ViewGroup): View = {
      val layoutView = if (convertView != null) convertView
      else getUi{ w[TextView] <~ padding( all = 4 dp) <~ Tweak{(view:TextView) => view.setTextSize(10 dp)}}

      val pm = getActivity.getPackageManager
      val resolveInfo = activities(position)
      layoutView.asInstanceOf[TextView].setText(resolveInfo.loadLabel(pm).toString)
     layoutView
    }

    override def getItem(pos:Int) = activities(pos)
    override def getItemId(pos:Int) = pos
  }
  
  override def onCreateView(inflator: LayoutInflater,
    parent: ViewGroup, savedBundleInstance: Bundle): View = {
    val startupIntent = new Intent(Intent.ACTION_MAIN)
    startupIntent.addCategory(Intent.CATEGORY_LAUNCHER)

    val pm = getActivity.getPackageManager
    val activities = pm.queryIntentActivities(startupIntent,0).asScala
    val sortedActivities = activities.sortBy(resolveInfo => resolveInfo.loadLabel(pm).toString)
    Log.d("$$$$$",""+sortedActivities.size)

    setListAdapter(new CustomerAdpater(sortedActivities.toList))
    super.onCreateView(inflator,parent,savedBundleInstance)
}

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = {
    super.onListItemClick(l, v, position, id)
    val resolveInfo = l.getAdapter.asInstanceOf[CustomerAdpater].getItem(position)
    val intent = new Intent(Intent.ACTION_MAIN)
    intent.setClassName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  }
}