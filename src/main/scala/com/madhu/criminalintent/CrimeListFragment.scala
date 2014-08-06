package com.madhu.criminalintent

import android.support.v4.app._
import android.os.Bundle
import android.widget.{TextView,ListView}
import android.widget.ArrayAdapter
import android.view.ViewGroup.LayoutParams._
import android.view.ViewGroup
import android.view.{ Gravity, View}


import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks
import macroid.contrib._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._



class CrimeListFragment extends ListFragment
with Contexts 
[ListFragment] with IdGeneration {
  var crimes:List[Crime] = _
  override def onCreate(savedBundleInstance:Bundle) = {
  	super.onCreate(savedBundleInstance)
  	getActivity().setTitle("List of crimes")
  	crimes = CrimeLab.getCrimes
  	var textView = getUi {
  		w[TextView] <~ wrapContent <~ id(123)
  	}  	

    class CustomAdapter(crimes:List[Crime]) 
    extends ArrayAdapter[Crime](getActivity(),0,crimes.asJava) {      
      override def getView(position:Int, 
      	convertView:View, parent:ViewGroup):View = {
         getUi{
          w[TextView] <~ text(crimes(position).mTitle) <~
           padding(top = 5 dp , bottom = 5 dp) 
         }
      }   	
    }
    val customAdapter = new CustomAdapter(crimes)  	
  	setListAdapter(customAdapter)
  }

  override def onListItemClick(l:ListView,v:View,position:Int,id:Long) ={
  	val crime = getListAdapter().getItem(position).
  	  asInstanceOf[Crime]
  	getUi{
  	  toast("clicked"+crime.mTitle) <~ gravity(
  			Gravity.TOP | Gravity.CENTER_HORIZONTAL ) <~fry
  	}
  }
}
