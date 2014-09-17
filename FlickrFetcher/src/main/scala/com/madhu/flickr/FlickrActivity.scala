package com.madhu.flickr

import android.os.Bundle
import android.support.v4.app._

// import macroid stuff
import macroid._
import macroid.FullDsl._
import macroid.contrib.LpTweaks._


class FlickrActivity extends FragmentActivity with
Contexts[FragmentActivity] with IdGeneration {
  
  override def onCreate(savedInstanceState: Bundle) = {  
    super.onCreate(savedInstanceState)
    val view = f[FlickrFragment].framed(Id.map,Tag.map) <~
    matchParent
    setContentView(getUi{view})
}
}

