package com.madhu.criminalintent

import android.os.Bundle
import android.widget.{
LinearLayout,
TextView,
Button,
EditText,
CheckBox
}
import android.widget.CompoundButton
import android.widget.CompoundButton._
import android.view.ViewGroup.LayoutParams._
import android.view._

import android.support.v4.app._
import java.util.UUID
import android.text.TextWatcher
import android.text.Editable
import android.content.Intent

// import macroid stuff

import macroid._
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib._

trait CustomTweaks {
  def margin(width: Int, height: Int)(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0, all: Int = -1) = {
    val layout = new LinearLayout.LayoutParams(width, height)
    if (all >= 0) {
      layout.setMargins(all, all, all, all)
    } else {
      layout.setMargins(left, top, right, bottom)
    }
    Tweak[View](_.setLayoutParams(layout))
  }

  def listSeperator(implicit context: AppContext): Tweak[TextView] = Tweak {
    (textView: TextView) => {
      textView.setAllCaps(true)
    }
  } + lp[LinearLayout](
    MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER_VERTICAL) +
    TextTweaks.bold + TextTweaks.size(6 sp) +
    padding(left = 8 dp)

}

object CrimeFragment {
  val EXTRA_CRIME_ID = "com.madhu.criminalintent.CrimeFragment.ID"

  def newInstance(uuid: UUID): CrimeFragment = {
    val argsBundle = new Bundle()
    argsBundle.putSerializable(EXTRA_CRIME_ID, uuid)
    val fragment = new CrimeFragment()
    fragment.setArguments(argsBundle)
    fragment
  }
}

class CrimeFragment extends Fragment with CustomTweaks
with Contexts[Fragment] {
  var crime: Crime = _
  var editText = slot[EditText]
  var checkBoxCrimeResolved = slot[CheckBox]

  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    val crimeId = getArguments().getSerializable(
      CrimeFragment.EXTRA_CRIME_ID).asInstanceOf[UUID]
    crime = CrimeLab.getCrime(crimeId).get
    /*crime = new Crime()
    crime.mDate = new Date()*/
  }

  override def onCreateView(inflator: LayoutInflater,
                            parent: ViewGroup, savedBundleInstance: Bundle): View = {
    val title = w[TextView] <~ text("Title") <~
      matchWidth <~ listSeperator
    val crimeInfo = w[EditText] <~ text(crime.mTitle) <~
      wire(editText) <~ matchWidth <~ margin(MATCH_PARENT,
      WRAP_CONTENT)(left = 16 dp, right = 16 dp) <~
      Tweak {
        (view: EditText) => {
          view.addTextChangedListener(
            new TextWatcher() {
              override def onTextChanged(c: CharSequence,
                                         start: Int, before: Int, count: Int) = {
                crime.mTitle = c.toString()
              }

              override def beforeTextChanged(c: CharSequence,
                                             start: Int, count: Int, after: Int) = {

              }

              override def afterTextChanged(e: Editable) = {

              }
            })
        }
      }

    val details = w[TextView] <~ text("Details") <~
      matchWidth <~ listSeperator
    val dateButton = w[Button] <~ text(crime.mDate.toString) <~
      disable
    val checkBox = w[CheckBox] <~ text("Crime solved") <~
      wire(checkBoxCrimeResolved) <~ Tweak {
      (view: CheckBox) => {
        view.setChecked(crime.solved)
      }
    } <~ Tweak {
      (view: CheckBox) => {
        view.setOnCheckedChangeListener(
          new OnCheckedChangeListener() {
            override def onCheckedChanged(
                                           buttonView: CompoundButton, isChecked: Boolean) {
              crime.solved = isChecked
            }
          })
      }
    }

    val portaitLayout = getUi {
      l[LinearLayout](
        title,
        crimeInfo,
        details,
        dateButton <~ margin(MATCH_PARENT,
          WRAP_CONTENT)(left = 16 dp, right = 16 dp),
        checkBox <~ margin(MATCH_PARENT,
          WRAP_CONTENT)(left = 16 dp, right = 16 dp)) <~ vertical <~ matchWidth
    }

    val landscapeLayout = getUi {
      l[LinearLayout](
        title,
        crimeInfo,
        details,
        l[LinearLayout](
          dateButton <~ lp[LinearLayout](0 dp,
            WRAP_CONTENT, 1.0f),
          checkBox <~ lp[LinearLayout](0 dp,
            WRAP_CONTENT, 1.0f)) <~ horizontal <~ matchWidth) <~ vertical <~ matchWidth
    }

    if(NavUtils.getParentActivityName(getActivity)!=null){
    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true)
    }

    setHasOptionsMenu(true)

    if (portrait) portaitLayout else landscapeLayout
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => {
        if(NavUtils.getParentActivityName(getActivity)!=null){
          NavUtils.navigateUpFromSameTask(getActivity)
        }
        /*val intent = new Intent(getActivity, classOf[CrimeListActivity])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)*/
        true
      }
      case _ => super.onOptionsItemSelected(item)
    }
  }
}