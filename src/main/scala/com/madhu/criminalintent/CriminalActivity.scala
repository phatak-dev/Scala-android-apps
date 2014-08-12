package com.madhu.criminalintent

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

class CriminalActivity extends FragmentActivity with Contexts[FragmentActivity] with IdGeneration {

  var frameLayout = slot[FrameLayout]
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val crimeID = getIntent().getSerializableExtra(CrimeFragment.
        EXTRA_CRIME_ID).asInstanceOf[
      UUID]
     
    val crimeFragment = CrimeFragment.newInstance(crimeID)
    val view = l[FrameLayout]() <~ id(Id.fragmentContainer) <~ 
     matchParent
    setContentView(getUi(view))

    /*verbose code as l[Fragment] seems to be broken
     sending bundle */
    val fm = getSupportFragmentManager()
    val fragment = fm.findFragmentById(Id.fragmentContainer)
    if(fragment == null) {
      fm.beginTransaction().add(Id.fragmentContainer, 
        crimeFragment)
        .commit()
    }
  }

}

