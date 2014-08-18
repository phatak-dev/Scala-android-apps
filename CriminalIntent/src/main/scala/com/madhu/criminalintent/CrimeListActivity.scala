package com.madhu.criminalintent

import android.support.v4.app._
import android.os.Bundle
import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks
import macroid.contrib._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CrimeListActivity extends FragmentActivity with Contexts[FragmentActivity] with IdGeneration {
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val view = f[CrimeListFragment].framed(Id.map, Tag.map) <~
      matchParent
    setContentView(getUi(view))

  }
}