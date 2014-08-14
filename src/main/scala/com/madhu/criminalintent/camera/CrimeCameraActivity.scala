package com.madhu.criminalintent.camera

import android.support.v4.app.FragmentActivity
import macroid.{IdGeneration, Contexts}
import android.os.Bundle
import macroid.FullDsl._
import macroid.contrib.LpTweaks.matchParent
import android.view.{WindowManager, Window}


/**
 * Created by madhu on 14/8/14.
 */
class CrimeCameraActivity extends FragmentActivity with Contexts[FragmentActivity] with IdGeneration {

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)
    getWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    val view = f[CrimeCameraFragment].framed(Id.crimeCamera,Tag.tag) <~ matchParent
    setContentView(getUi(view))
    }
}
