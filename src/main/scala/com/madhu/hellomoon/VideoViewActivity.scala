package com.madhu.hellomoon


import android.support.v4.app._
import android.widget.{MediaController, VideoView, LinearLayout}
import android.net.Uri
import android.os.Bundle

import macroid._
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import android.app.Activity

class VideoViewActivity extends Activity with
Contexts[Activity] with IdGeneration{

 val url = Uri.parse("android.resource://com.madhu.hellomoon/raw/small")

  var video = slot[VideoView]
  override def onCreate(savedInstanceState: Bundle) = {  
    super.onCreate(savedInstanceState)
    val view = w[VideoView] <~ matchParent <~
      Tweak { (
      (view:VideoView) => {
      	view.setVideoURI(url)
        val mediaController = new MediaController(this)
        mediaController.setAnchorView(view)
        view.setMediaController(mediaController)
        view.start()
      }
    )}	

    setContentView(getUi{view})

  }



}