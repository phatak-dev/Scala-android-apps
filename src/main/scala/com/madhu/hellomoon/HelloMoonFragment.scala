package com.madhu.hellomoon

import android.os.Bundle
import android.widget.{
  TextView,
  Button,
  FrameLayout,
  TableLayout,
  ImageView ,
  TableRow 
}
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
import macroid.contrib.ImageTweaks
import macroid.contrib._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
class HelloMoonFragment extends Fragment 
  with Contexts[Fragment] {  
  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    
  }

  val audioPlayer = new AudioPlayer()
  override def onCreateView(inflator: LayoutInflater,
    parent: ViewGroup, savedBundleInstance: Bundle): View = {
     val view = l[TableLayout](
     w[ImageView] <~ ImageTweaks.res(R.drawable.
        armstrong_on_moon) <~ 
      Tweak { (view:ImageView) => view.setScaleType(
        ImageView.ScaleType.CENTER_INSIDE) } <~ Tweak {
       (view:View) => {
          val layoutParams = new TableLayout.LayoutParams(
            MATCH_PARENT,MATCH_PARENT,1.0f)          
          view.setLayoutParams(layoutParams)
         }
      }       ,
      l[TableRow] (
        w[Button] <~ text("play") <~ lp[TableRow](
          WRAP_CONTENT,WRAP_CONTENT) <~ On.click {
            audioPlayer.play(getActivity())
            Ui(true)
          },
        w[Button] <~ text("stop") <~ On.click{
          audioPlayer.stop()
          Ui(true)
        } <~ lp[TableRow](
          WRAP_CONTENT,WRAP_CONTENT)
      ) <~ Tweak {
         (view:View) => {
          val layoutParams = new TableLayout.LayoutParams(
            WRAP_CONTENT,WRAP_CONTENT,0)
          layoutParams.gravity = Gravity.CENTER_HORIZONTAL |
            Gravity.BOTTOM
          view.setLayoutParams(layoutParams)
         }
        }
    ) <~ matchParent
    getUi(view) 
  }
}