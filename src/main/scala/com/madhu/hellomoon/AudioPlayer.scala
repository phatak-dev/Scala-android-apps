package com.madhu.hellomoon

import android.media.MediaPlayer
import android.content.Context

class AudioPlayer {
  var mPlayer:MediaPlayer = _
  def stop() = {
  	if(mPlayer !=null ) {
  	  mPlayer.release()
  	  mPlayer=null
  	}
  }

 def play(context:Context) = {
  stop()
  mPlayer = MediaPlayer.create(context,
  	R.raw.one_small_step)
  mPlayer.start()
 }
}