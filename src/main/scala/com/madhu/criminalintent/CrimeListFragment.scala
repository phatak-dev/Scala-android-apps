package com.madhu.criminalintent

import android.support.v4.app._
import android.os.Bundle
import android.widget.{TextView,ListView,
RelativeLayout,CheckBox}
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
import android.util.Log.d



class CrimeListFragment extends ListFragment
with Contexts 
[ListFragment] with IdGeneration {
  var crimes:List[Crime] = _
  override def onCreate(savedBundleInstance:Bundle) = {
  	super.onCreate(savedBundleInstance)
  	getActivity().setTitle("List of crimes")
  	crimes = CrimeLab.getCrimes
  	var checkBox = slot[CheckBox]
  	def layout = getUi{l[RelativeLayout](
  		  w[CheckBox] <~ wire(checkBox) 
  		   <~ Tweak{ (view:CheckBox) => view.setGravity(Gravity.CENTER)} <~ 
  		  padding(all = 4 dp) <~ id(Id.checkBox) <~ Tweak {
  		  	(view:CheckBox) => {
  		  	   val layoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT,
  		  	   MATCH_PARENT)
  		  	   layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)  		  	   
  		  	   view.setLayoutParams(layoutParams)    		  	     	  		  	   
  		  	}
  		  }
  		  ,
  		  w[TextView] <~ Tweak {
  		  	(view:View) => {
  		  	   val layout = new RelativeLayout.LayoutParams(
  		  	   MATCH_PARENT,WRAP_CONTENT)  		  		  		  	   
  		  	   layout.addRule(RelativeLayout.LEFT_OF,
  		  	   	Id.checkBox)
  		  	   view.setLayoutParams(layout)    		  	   
  		  	}} <~ TextTweaks.bold <~ id(Id.crimeText) <~ 
  		  	padding ( left = 4 dp, right = 4 dp),

  		  	w[TextView] <~ Tweak {
  		  	(view:View) => {
  		  	   val layout = new RelativeLayout.LayoutParams(
  		  	   MATCH_PARENT,WRAP_CONTENT)  		  		  		  	   
  		  	   layout.addRule(RelativeLayout.LEFT_OF,
  		  	   	Id.checkBox)
  		  	   layout.addRule(RelativeLayout.BELOW,
  		  	   	Id.crimeText)
  		  	   view.setLayoutParams(layout)    		  	   		  	  
  		  	}} <~ padding ( left = 4 dp, right = 4 dp , top= 4 dp)
  		  	<~ id(Id.dateText)

  		) <~ lp[android.widget.AbsListView](   
        MATCH_PARENT,WRAP_CONTENT) }        
  	 

    class CustomAdapter(crimes:List[Crime]) 
    extends ArrayAdapter[Crime](getActivity(),0,crimes.asJava) {      
      override def getView(position:Int, 
      	convertView:View, parent:ViewGroup):View = {      	      	       
        val layoutView = if(convertView !=null) convertView
        else layout         	        
        val crime = getListAdapter().getItem(position).asInstanceOf[
         Crime]    
        val textView = layoutView.find[TextView](Id.crimeText)                        
        val dateText = layoutView.find[TextView](Id.dateText)                
        val checkBox = layoutView.find[CheckBox](Id.checkBox)
       
        getUi{textView <~ text(crime.mTitle)} 
        getUi{dateText <~ text(crime.mDate.toString)} 
        getUi{checkBox <~ Tweak {(view:CheckBox) =>{
        	view.setChecked(crime.solved) }                       
       }}
        layoutView                      
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
