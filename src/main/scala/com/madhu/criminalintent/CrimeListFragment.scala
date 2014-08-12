package com.madhu.criminalintent

import android.support.v4.app._
import android.os.Bundle
import android.widget.{
TextView,
ListView,
RelativeLayout,
CheckBox
}
import android.widget.ArrayAdapter
import android.view.ViewGroup.LayoutParams._
import android.view._

import macroid._
import macroid.FullDsl._
import macroid.contrib._
import scala.collection.JavaConverters._
import android.util.Log.d
import android.view.MenuItem.OnMenuItemClickListener
import android.content.Intent


trait MenuHelpers {

  implicit class MenuOps(item:MenuItem)(implicit appContext:
  AppContext) {
    def onClick(fn: => (MenuItem => Boolean)) = item.setOnMenuItemClickListener(
      new OnMenuItemClickListener {
        override def onMenuItemClick(item: MenuItem) = {
          fn(item)
        }
      }
    )
    implicit def toMenuItem(item:MenuItem) = new MenuOps(item)
  }


}


class CrimeListFragment extends ListFragment
with Contexts[ListFragment] with IdGeneration with MenuHelpers {

  val tag = "com.madhu.criminalintent.CrimeListFragment.Tag"

  class CustomAdapter(crimes: List[Crime],
                      layout: => View)
    extends ArrayAdapter[Crime](getActivity(), 0, crimes.asJava) {
    override def getView(position: Int,
                         convertView: View, parent: ViewGroup): View = {
      val layoutView = if (convertView != null) convertView
      else layout
      val crime = getListAdapter().getItem(position).asInstanceOf[Crime]
      val textView = layoutView.find[TextView](Id.crimeText)
      val dateText = layoutView.find[TextView](Id.dateText)
      val checkBox = layoutView.find[CheckBox](Id.checkBox)

      getUi {
        textView <~ text(crime.mTitle)
      }
      getUi {
        dateText <~ text(crime.mDate.toString)
      }
      getUi {
        checkBox <~ Tweak {
          (view: CheckBox) => {
            view.setChecked(crime.solved)
          }
        }
      }
      layoutView
    }

  }


  var crimes: List[Crime] = _

  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    getActivity().setTitle("List of crimes")
    crimes = CrimeLab.getCrimes
    var checkBox = slot[CheckBox]
    def layout = getUi {
      l[RelativeLayout](
        w[CheckBox] <~ wire(checkBox)
          <~ Tweak {
          (view: CheckBox) => view.setGravity(Gravity.CENTER)
        } <~
          padding(all = 4 dp) <~ id(Id.checkBox) <~ Tweak {
          (view: CheckBox) => {
            val layoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT,
              MATCH_PARENT)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            view.setLayoutParams(layoutParams)
          }
        } <~ disable <~ Tweak {
          (view: CheckBox) => view.setFocusable(false)
        },
        w[TextView] <~ Tweak {
          (view: View) => {
            val layout = new RelativeLayout.LayoutParams(
              MATCH_PARENT, WRAP_CONTENT)
            layout.addRule(RelativeLayout.LEFT_OF,
              Id.checkBox)
            view.setLayoutParams(layout)
          }
        } <~ TextTweaks.bold <~ id(Id.crimeText) <~
          padding(left = 4 dp, right = 4 dp),

        w[TextView] <~ Tweak {
          (view: View) => {
            val layout = new RelativeLayout.LayoutParams(
              MATCH_PARENT, WRAP_CONTENT)
            layout.addRule(RelativeLayout.LEFT_OF,
              Id.checkBox)
            layout.addRule(RelativeLayout.BELOW,
              Id.crimeText)
            view.setLayoutParams(layout)
          }
        } <~ padding(left = 4 dp, right = 4 dp, top = 4 dp)
          <~ id(Id.dateText)) <~ lp[android.widget.AbsListView](
        MATCH_PARENT, WRAP_CONTENT)
    }
    val customAdapter = new CustomAdapter(crimes, layout)
    setListAdapter(customAdapter)
    setHasOptionsMenu(true)

    d(tag, "oncreate")
  }


  override def onListItemClick(l: ListView, v: View, position: Int, id: Long) = {
    val crime = getListAdapter().getItem(position).
      asInstanceOf[Crime]
    import android.content.Intent
    val intent = new Intent(getActivity(), classOf[
      CrimePagerActivity])
    intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.uuid)
    startActivity(intent)
  }

  override def onResume() = {
    super.onResume()
    (getListAdapter().asInstanceOf[CustomAdapter]).notifyDataSetChanged();
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    super.onCreateOptionsMenu(menu, inflater)
    val addCrimeMenuItem = menu.add("New crime")
    addCrimeMenuItem.onClick( (item:MenuItem) => {
      d(tag," on click called")
      /*getUi{toast("menu item clicked") <~ gravity(Gravity.TOP | Gravity.CENTER_VERTICAL) <~ fry
       }; true*/
      val crime = new Crime()
      CrimeLab.addCrime(crime)
      val intent = new Intent(getActivity,classOf[CrimePagerActivity])
      intent.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.uuid)
      startActivityForResult(intent,0)
      true
    })


    addCrimeMenuItem.setIcon(android.R.drawable.ic_menu_add).
      setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT |
      MenuItem.SHOW_AS_ACTION_IF_ROOM)
    menu.add("Show subtitle").setIcon(android.R.drawable.btn_minus).onClick((item:MenuItem) =>{
       if(getActivity.getActionBar.getSubtitle==null) {
         getActivity.getActionBar.setSubtitle("Subtitle")
         item.setTitle("Hide subtitle")
       }
      else {
         getActivity.getActionBar.setSubtitle(null)
         item.setTitle("Show subtitle")
       }
      true
      })
     .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

  }
}
