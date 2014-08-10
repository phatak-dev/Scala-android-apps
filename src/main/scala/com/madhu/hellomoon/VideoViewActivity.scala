package com.madhu.hellomoon


import android.support.v4.app._
import android.widget.{VideoView,LinearLayout}
import android.net.Uri
import android.os.Bundle

import macroid._
import macroid.Ui
import macroid.FullDsl._
import macroid.contrib.LpTweaks._

class VideoViewActivity extends FragmentActivity with 
Contexts[FragmentActivity] with IdGeneration{

 val url = Uri.parse("android:resource://"+"com.madhu.hellomoon/"+
 	"raw/sample_mpeg4")

  override def onCreate(savedInstanceState: Bundle) = {  
    super.onCreate(savedInstanceState)
    val view = l[LinearLayout] {	
      w[VideoView] <~ matchParent <~
      Tweak { (
      (view:VideoView) => {
      	view.setVideoURI(url)
      	view.start()
      }
    )}	
    } <~ matchParent 
    setContentView(getUi{view})
  }
}