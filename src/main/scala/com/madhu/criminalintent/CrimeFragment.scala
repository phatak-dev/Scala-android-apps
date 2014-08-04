package com.madhu.criminalintent

import android.os.Bundle
import android.widget.{LinearLayout, TextView, Button,FrameLayout,
EditText}
import android.view.ViewGroup.LayoutParams._
import android.view.ViewGroup
import android.view.{Gravity, View}
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

// import macroid stuff
import macroid._
import macroid.util.Ui
import macroid.FullDsl._
import macroid.contrib.ExtraTweaks._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



class CrimeFragment extends Fragment with Contexts
[Fragment] {
  var  crime:Crime  = _
  var editText = slot[EditText]
  override def onCreate(savedBundleInstance:Bundle) = {
  	super.onCreate(savedBundleInstance)
  	crime = new Crime(mTitle="test")
  }
  override def onCreateView(inflator:LayoutInflater,
  	parent:ViewGroup,savedBundleInstance:Bundle):View = {
    val view = getUi{
    l[LinearLayout]{
      w[EditText] <~ text("something") <~ wire(editText)
      }      
    }
    getUi {
     editText.get.addTextChangedListener(
     	new TextWatcher() {
     		override def onTextChanged(c:CharSequence,
     		start:Int,before:Int,count:Int) = {
     			crime.mTitle = c.toString()
     			//d("tag","onTextChanged called")
     		}
     		override def beforeTextChanged(c:CharSequence,
     		start:Int,count:Int,after:Int) = {

     		}
     		override def afterTextChanged(e:Editable) ={

     		}
     	}
      )

      Ui(true)	
    }
    view       
  }


}