package com.madhu.taskkiller

import android.os.Bundle
import android.view._


import macroid.FullDsl._
import android.content.{Context, Intent}
import android.support.v4.app._
import android.util.Log
import scala.collection.JavaConverters._
import android.widget._
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.view.MenuItem.OnMenuItemClickListener
import android.view.ViewGroup.LayoutParams._
import android.widget.LinearLayout.LayoutParams


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

  def layout() = {
    l[LinearLayout](
      w[TextView] <~
      id(Id.applicationName) <~
      padding( all = 4 dp) <~
     Tweak{(view:TextView) => view.setTextSize(5 dp)} <~ lp[LinearLayout](WRAP_CONTENT,WRAP_CONTENT,1),
     w[Button] <~ text("Kill ") <~
     id(Id.killButton) <~ Tweak{
       (view:View) => {
         val layout = new LayoutParams(WRAP_CONTENT,WRAP_CONTENT,0)
         layout.gravity = Gravity.RIGHT
         view.setLayoutParams(layout)
       }
     }
    ) <~ horizontal
  }

  class CustomerAdpater(var activities:List[RunningAppProcessInfo]) extends BaseAdapter{
    override def getCount: Int = activities.size
    override def getView(position: Int,
                         convertView: View, parent: ViewGroup): View = {
      val runningTaskInfo = activities(position)
      val layoutView = if (convertView != null) convertView
      else getUi{ layout}
      val appName = layoutView.findViewById(Id.applicationName).asInstanceOf[TextView]
      val killButton = layoutView.findViewById(Id.killButton).asInstanceOf[Button]
       getUi { Ui { killButton } <~
         On.click {
           val am = getActivity.getSystemService(Context.ACTIVITY_SERVICE).asInstanceOf[ActivityManager]
           Log.d("$$$$$$$",runningTaskInfo.processName)
           am.killBackgroundProcesses(runningTaskInfo.pkgList(0))
           val adpater = getListAdapter.asInstanceOf[CustomerAdpater]
           val newRunningProcess = adpater.activities.filter(value => value.uid != runningTaskInfo.uid)
           adpater.activities = newRunningProcess
           adpater.notifyDataSetChanged()
           Ui(true)
       } }

      appName.setText(""+runningTaskInfo.processName)
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

    val processes = am.getRunningAppProcesses.asScala.toList//filter(value => !value.processName.startsWith("com.android") )
    setListAdapter(new CustomerAdpater(processes))
    super.onCreateView(inflator,parent,savedBundleInstance)

   super.onCreateView(inflator,parent,savedBundleInstance)
}




}