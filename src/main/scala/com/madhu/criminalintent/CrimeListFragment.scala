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


class CrimeListFragment extends ListFragment
with Contexts
[ListFragment] {
  var crimes:List[Crime] = _
  override def onCreate(savedBundleInstance:Bundle) = {
  	super.onCreate(savedBundleInstance)
  	getActivity().setTitle("List of crimes")
  	crimes = CrimeLab.getCrimes
  }
}
