package com.madhu.neardlauncher

import android.os.Bundle
import android.view._


import macroid.FullDsl._
import android.content.{Context, Intent}
import android.support.v4.app._
import android.util.Log
import scala.collection.JavaConverters._
import android.widget._
import android.app.ActivityManager
import android.app.ActivityManager.{RunningAppProcessInfo, RunningTaskInfo}
import android.view.MenuItem.OnMenuItemClickListener

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


class ProcessManagerFragment extends ListFragment
  with Contexts[ListFragment] with IdGeneration with MenuHelpers{
  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    
  }

  def layout = {
    l[LinearLayout](
      w[TextView] <~
      id(Id.applicationName) <~
      padding( all = 4 dp) <~
     Tweak{(view:TextView) => view.setTextSize(5 dp)},
     w[Button] <~ text("Kill ") <~ id(Id.killButton)
    ) <~ horizontal
  }

  class CustomerAdpater(val activities:List[RunningAppProcessInfo]) extends BaseAdapter{
    override def getCount: Int = activities.size
    override def getView(position: Int,
                         convertView: View, parent: ViewGroup): View = {
      val layoutView = if (convertView != null) convertView
      else getUi{ layout}
      val runningTaskInfo = activities(position)
      val appName = layoutView.findViewById(Id.applicationName).asInstanceOf[TextView]
      appName.setText(""+runningTaskInfo.processName)

      val killButton = layoutView.find[Button](Id.killButton)
      getUi{ killButton  <~ On.click {
        Log.d("$$$$$$","kill on"+runningTaskInfo.pid+"called")
        android.os.Process.killProcess(runningTaskInfo.pid)
        Ui(true)
      }}
     layoutView
    }

    override def getItem(pos:Int) = activities(pos)
    override def getItemId(pos:Int) = pos
  }
  
  override def onCreateView(inflator: LayoutInflater,
    parent: ViewGroup, savedBundleInstance: Bundle): View = {
    val startupIntent = new Intent(Intent.ACTION_MAIN)
    startupIntent.addCategory(Intent.CATEGORY_LAUNCHER)

    val am = getActivity.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]

    val processes = am.getRunningAppProcesses.asScala.toList
    setListAdapter(new CustomerAdpater(processes))
    super.onCreateView(inflator,parent,savedBundleInstance)

   super.onCreateView(inflator,parent,savedBundleInstance)
}



  /*override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = {
    super.onListItemClick(l, v, position, id)
    val resolveInfo = l.getAdapter.asInstanceOf[CustomerAdpater].getItem(position)
    val intent = new Intent(Intent.ACTION_MAIN)
    intent.setClassName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  }*/
  /*override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo): Unit  = {
    menu.add("kill").onClick((menuItem:MenuItem) => {
      val info = menuItem.getMenuInfo.asInstanceOf[AdapterContextMenuInfo]
      val position = info.position
      val am = getActivity.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]
      val processInfo = getListAdapter.asInstanceOf[CustomerAdpater].getItem(position)
      am.


     true
    })

    super.onCreateContextMenu(menu, v, menuInfo)
  }*/
}