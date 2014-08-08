package com.madhu.hellomoon

import android.media.MediaPlayer
import android.content.Context
import macroid._
import macroid.FullDsl._

class AudioPlayer {
  var mPlayer:MediaPlayer = _
  var length:Int = -1
  def stop() = {
  	if(mPlayer !=null ) {
  	  mPlayer.release()
  	  mPlayer=null
  	  length = -1
  	}
  }

  def pause() = {
  	if(mPlayer!=null) {
  	 mPlayer.pause()
  	 length = mPlayer.getCurrentPosition()
  	}
  }

 def play(context:Context) = {
  if(length != -1){
  	mPlayer.seekTo(length)
  } else {
  stop()
  mPlayer = MediaPlayer.create(context,
  	R.raw.one_small_step)
  mPlayer.setOnCompletionListener {
  	new MediaPlayer.OnCompletionListener{
  	  override def onCompletion(mp:MediaPlayer) = {
  	  	stop()
  	  }
  	}
  }
  }  
  mPlayer.start()
 }
}