package com.madhu.criminalintent

import android.os.Bundle
import android.widget.{
  LinearLayout,
  TextView,
  Button,
  FrameLayout,
  EditText,
  CheckBox
}
import android.widget.CompoundButton
import android.widget.CompoundButton._
import android.view.ViewGroup.LayoutParams._
import android.view.ViewGroup
import android.view.{ Gravity, View }
import android.app.Activity
import android.text.method.LinkMovementMethod;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.Spanned;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log.d
import android.content.Intent
import android.support.v4.app._
import java.util.UUID
import android.view.LayoutInflater
import android.text.TextWatcher
import android.text.Editable
import java.util.Date

// import macroid stuff
import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks
import macroid.contrib._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
    (textView: TextView) =>
      {
        textView.setAllCaps(true)
      }
  } + lp[LinearLayout](
    MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER_VERTICAL) +
    TextTweaks.bold + TextTweaks.size(6 sp) +
    padding(left = 8 dp)

}

class CrimeFragment extends Fragment with CustomTweaks
  with Contexts[Fragment] {
  var crime: Crime = _
  var editText = slot[EditText]
  var checkBoxCrimeResolved = slot[CheckBox]
  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    crime = new Crime()
    crime.mDate = new Date()
  }
  override def onCreateView(inflator: LayoutInflater,
    parent: ViewGroup, savedBundleInstance: Bundle): View = {
    val title = w[TextView] <~ text("Title") <~
      matchWidth <~ listSeperator
    val crimeInfo = w[EditText] <~ text("something") <~
      wire(editText) <~ matchWidth <~ margin(MATCH_PARENT,
        WRAP_CONTENT)(left = 16 dp, right = 16 dp)
    val details = w[TextView] <~ text("Details") <~
      matchWidth <~ listSeperator
    val dateButton = w[Button] <~ text(crime.mDate.toString) <~
      disable
    val checkBox = w[CheckBox] <~ text("Crime solved") <~
      wire(checkBoxCrimeResolved)

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

    /* set event listener */
    getUi {
      editText.get.addTextChangedListener(
        new TextWatcher() {
          override def onTextChanged(c: CharSequence,
            start: Int, before: Int, count: Int) = {
            crime.mTitle = c.toString()
            //d("tag","onTextChanged called")
          }
          override def beforeTextChanged(c: CharSequence,
            start: Int, count: Int, after: Int) = {

          }
          override def afterTextChanged(e: Editable) = {

          }
        })

      checkBoxCrimeResolved.get.setOnCheckedChangeListener(
        new OnCheckedChangeListener() {
          override def onCheckedChanged(
            buttonView: CompoundButton, isChecked: Boolean) {
            crime.solved = isChecked
          }
        })

      Ui(true)
    }
    if (portrait) portaitLayout else landscapeLayout
  }

}