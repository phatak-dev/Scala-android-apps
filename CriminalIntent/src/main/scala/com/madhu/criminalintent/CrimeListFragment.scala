package com.madhu.criminalintent

import android.support.v4.app._
import android.os.{Build, Bundle}
import android.widget._
import android.view.ViewGroup.LayoutParams._


import macroid._
import macroid.contrib.LpTweaks._
import macroid.FullDsl._
import macroid.contrib._
import scala.collection.JavaConverters._
import android.util.Log.d
import android.view.MenuItem.OnMenuItemClickListener
import android.content.Intent
import android.widget.AbsListView.MultiChoiceModeListener
import android.view._
import macroid.AppContext
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import android.graphics.drawable.StateListDrawable
import android.app.Activity


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

trait CallBacks {
  def onCrime(crime:Crime)
}


class CrimeListFragment extends ListFragment
with Contexts[ListFragment] with IdGeneration with MenuHelpers {

  val tag = "com.madhu.criminalintent.CrimeListFragment.Tag"
  var crimes: List[Crime] = _
  var callBacks:Option[CallBacks] = None

  class CustomAdapter(layout: => View,var crimes:List[Crime])
    extends BaseAdapter{
    override def getCount: Int = crimes.size
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
   override def getItem(pos:Int) = crimes(pos)
   override def getItemId(pos:Int) = pos
  }




  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    getActivity().setTitle("List of crimes")
    crimes = CrimeLab(getActivity).getCrimes
    var checkBox = slot[CheckBox]

    def layout = getUi {
      l[RelativeLayout](
        w[CheckBox] <~
          wire(checkBox) <~
          Tweak {
            (view: CheckBox) => view.setGravity(Gravity.CENTER)
          } <~
          padding(all = 4 dp) <~
          id(Id.checkBox) <~
          Tweak {
            (view: CheckBox) => {
              val layoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT,
                MATCH_PARENT)
              layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
              view.setLayoutParams(layoutParams)
            }
          } <~
          disable <~
          Tweak {
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
        } <~
          TextTweaks.bold <~
          id(Id.crimeText) <~
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
        } <~
          padding(left = 4 dp, right = 4 dp, top = 4 dp) <~
          id(Id.dateText)) <~
        lp[android.widget.AbsListView](MATCH_PARENT, WRAP_CONTENT) <~
        Tweak((relativeLayout:RelativeLayout) => {
          val stateListDrawable = new StateListDrawable()
          stateListDrawable.addState(Array[Int](android.R.attr.state_activated),
            getResources.getDrawable(android.R.color.darker_gray))
          relativeLayout.setBackground(stateListDrawable)
        })

    }


    val customAdapter = new CustomAdapter(layout,crimes)
    setListAdapter(customAdapter)
    setHasOptionsMenu(true)




    d(tag, "oncreate")
  }


  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    d(tag,"activity created")

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      registerForContextMenu(this.getListView)
    } else {
    this.getListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL)
    this.getListView.findViewById(android.R.id.list).asInstanceOf[ListView].setMultiChoiceModeListener(new MultiChoiceModeListener {

      override def  onItemCheckedStateChanged(actionMode:ActionMode,position:Int,id:Long,checked:Boolean):Unit = {}

      override def  onCreateActionMode(actionMode:ActionMode,menu:Menu)= {
        menu.add("Delete").setIcon(android.R.drawable.ic_menu_delete).onClick(
          (item:MenuItem) => {
            val adapter = getListAdapter.asInstanceOf[CustomAdapter]
            val positions = crimes.zipWithIndex.map(_._2).flatMap(
             pos => if(getListView.isItemChecked(pos)) List(pos) else List()
            )
            positions.foreach(position => CrimeLab(getActivity).deleteCrime(
                crimes(position)
            ))
            actionMode.finish()
            crimes = CrimeLab(getActivity).getCrimes()
            adapter.crimes = crimes
            adapter.notifyDataSetChanged()
            true
          }
        )
       true
      }

      override def  onActionItemClicked(mode:ActionMode,menuItem:MenuItem) = {

        true
      }

      override def  onDestroyActionMode(mode:ActionMode) = {

      }

      override def onPrepareActionMode(action:ActionMode,menu:Menu)= {
        false
      }

    })
    }




    super.onActivityCreated(savedInstanceState)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
   val v =  super.onCreateView(inflater, container, savedInstanceState)
    val emptyView = l[LinearLayout](
      w[TextView] <~
        text("No crimes in the list"),

      w[Button] <~
        text("Add crime") <~
        On.click {
          val crime = new Crime()
          CrimeLab(getActivity).addCrime(crime)
          val intent = new Intent(getActivity, classOf[CrimePagerActivity])
          intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.uuid)
          startActivityForResult(intent, 0)
          Ui(true)
        } <~
        lp[LinearLayout](WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER_HORIZONTAL)

    ) <~
      Tweak {
        (view: View) => view.setId(android.R.id.empty)
      } <~
      vertical <~
      lp[FrameLayout](WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER_HORIZONTAL |
        Gravity.CENTER_VERTICAL)


    val listView = w[ListView] <~
      matchParent <~
      Tweak {
        (view: ListView) => {
          view.setId(android.R.id.list)
        }
      }

    val layout = l[FrameLayout](
      listView,
      emptyView
    )

    getUi(layout)
  }

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long) = {
    val crime = getListAdapter().getItem(position).
      asInstanceOf[Crime]
    /*import android.content.Intent
    val intent = new Intent(getActivity(), classOf[
      CrimePagerActivity])
    intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.uuid)
    startActivity(intent)*/
    callBacks.map(callBack => callBack.onCrime(crime))
  }



  override def onResume() = {
    super.onResume()
    crimes = CrimeLab(getActivity).getCrimes()
    val adapter = getListAdapter.asInstanceOf[CustomAdapter]
    adapter.crimes = crimes
    adapter.notifyDataSetChanged()
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    super.onCreateOptionsMenu(menu, inflater)
    val addCrimeMenuItem = menu.add("New crime")
    addCrimeMenuItem.onClick((item: MenuItem) => {
      d(tag, " on click called")
      val crime = new Crime()
      CrimeLab(getActivity).addCrime(crime)
      val intent = new Intent(getActivity, classOf[CrimePagerActivity])
      intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.uuid)
      startActivityForResult(intent, 0)
      true
    })


    addCrimeMenuItem.setIcon(android.R.drawable.ic_menu_add).
      setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT |
      MenuItem.SHOW_AS_ACTION_IF_ROOM)
    menu.add("Show subtitle").setIcon(android.R.drawable.btn_minus).onClick((item: MenuItem) => {
      if (getActivity.getActionBar.getSubtitle == null) {
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

  override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo): Unit = {
    menu.add("Delete").onClick(item => {
      val info = item.getMenuInfo.asInstanceOf[AdapterContextMenuInfo]
      val position = info.position
      val crime = crimes(position)
      CrimeLab(getActivity).deleteCrime(crime)
      crimes = CrimeLab(getActivity).getCrimes()
      val adapter = getListAdapter.asInstanceOf[CustomAdapter]
      adapter.crimes = crimes
      adapter.notifyDataSetChanged()
      true
    })
    super.onCreateContextMenu(menu, v, menuInfo)
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    callBacks = Some(getActivity.asInstanceOf[CallBacks])
  }


  override def onDetach(): Unit = {
    super.onDetach()
    callBacks = None
  }

}
