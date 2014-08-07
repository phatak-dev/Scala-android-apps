package com.madhu.criminalintent

import android.os.Bundle

import android.support.v4.app._
import android.support.v4.view.ViewPager
import java.util.UUID
import android.view.ViewGroup.LayoutParams._
import android.widget.FrameLayout
import android.view.ViewGroup

import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks
import macroid.contrib._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CrimePagerActivity extends FragmentActivity with Contexts[FragmentActivity] with IdGeneration {
  var viewPager: ViewPager = _
  var crimes: List[Crime] = _

  def toPx(dp: Int) = {
    import android.util.TypedValue
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
      dp, getResources().getDisplayMetrics()).toInt
  }

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    val crimeID = getIntent().getSerializableExtra(CrimeFragment.
      EXTRA_CRIME_ID).asInstanceOf[UUID]
    viewPager = new ViewPager(this)
    viewPager.setId(Id.viewPager)

    setContentView(viewPager)

    /* As ViewPage takes full screen
    control its height manually*/
    val layoutParams = new FrameLayout.LayoutParams(
      MATCH_PARENT, MATCH_PARENT, 1)
    layoutParams.height = toPx(300)
    viewPager.setLayoutParams(layoutParams)

    crimes = CrimeLab.getCrimes()    

    val fm = getSupportFragmentManager()
    viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
      override def getCount: Int = {
        return crimes.size
      }
      override def getItem(pos: Int): Fragment = {
        val crime = crimes(pos)
        CrimeFragment.newInstance(crime.uuid)
      }
    })

    val index = crimes.zipWithIndex.filter(
    	_._1.uuid == crimeID).map(_._2).head
    viewPager.setCurrentItem(index)

  }

}