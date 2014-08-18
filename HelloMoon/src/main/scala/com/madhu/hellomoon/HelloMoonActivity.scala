package com.madhu.hellomoon

import android.os.Bundle
import android.widget.{ LinearLayout, TextView, Button, FrameLayout }
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
import java.util.Date

// import macroid stuff
import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class HelloMoonActivity extends FragmentActivity with 
Contexts[FragmentActivity] with IdGeneration {
  
  override def onCreate(savedInstanceState: Bundle) = {  
    super.onCreate(savedInstanceState)
    val view = f[HelloMoonFragment].framed(Id.map,Tag.map) <~
    matchParent
    setContentView(getUi{view})
}
}

