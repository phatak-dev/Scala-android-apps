package com.madhu.criminalintent

import android.support.v4.app._
import android.os.Bundle
import macroid._
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import android.widget.{FrameLayout, LinearLayout}
import android.view.ViewGroup.LayoutParams._
import android.content.Intent

class CrimeListActivity extends FragmentActivity with Contexts[FragmentActivity] with IdGeneration with CallBacks{
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val view = f[CrimeListFragment].framed(Id.map, Tag.map) <~
      matchParent

    val tabletLayout = l[LinearLayout](
      f[CrimeListFragment].framed(Id.first,Tag.first) <~ lp[LinearLayout](0 dp,MATCH_PARENT,1),
      l[FrameLayout]() <~ id(Id.crimeDetails) <~ lp[LinearLayout](0 dp,MATCH_PARENT,3) <~ id(Id.detailFragmentContainer)
    ) <~ matchParent

    val layout = widerThan(600 dp) ? tabletLayout | view

    setContentView(getUi(layout))

  }

  override def onCrime(crime: Crime): Unit = {

    if(findViewById(Id.detailFragmentContainer)==null) {
      val i = new Intent(this, classOf[CrimePagerActivity])
      i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.uuid)
      startActivity(i)
    }
    else {
      val fragmentManager = getSupportFragmentManager
      val ft = fragmentManager.beginTransaction()

      val oldDetail = fragmentManager.findFragmentById(Id.detailFragmentContainer)
      val newDetail = CrimeFragment.newInstance(crime.uuid)
      if(oldDetail!=null) {
        ft.remove(oldDetail)
      }

      ft.add(Id.detailFragmentContainer,newDetail)
      ft.commit()
    }



  }
}