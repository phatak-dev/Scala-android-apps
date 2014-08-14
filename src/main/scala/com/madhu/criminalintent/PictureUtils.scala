package com.madhu.criminalintent

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.BitmapFactory
import android.widget.ImageView

/**
 * Created by madhu on 14/8/14.
 */
object PictureUtils {

  def getScaledDrawable(activity:Activity,path:String):BitmapDrawable = {
    val display = activity.getWindowManager.getDefaultDisplay
    val displayWidth = display.getWidth
    val displayHeight = display.getHeight

    val options = new BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path,options)

    val sourceWidth = options.outWidth
    val sourceHeight = options.outHeight

    var inSampleSize = 1

    if (sourceHeight > displayHeight || sourceWidth > displayWidth) {
      if (sourceWidth > sourceHeight) {
        inSampleSize = Math.round(sourceHeight / displayHeight)
      } else {
        inSampleSize = Math.round(sourceWidth / displayWidth)
      }
    }

    val newOptions = new BitmapFactory.Options()
    newOptions.inSampleSize = inSampleSize

    val bitmap = BitmapFactory.decodeFile(path,newOptions)
    new BitmapDrawable(activity.getResources,bitmap)

  }

  def cleanUpImage(imageView:Option[ImageView])= {
    imageView.map(view => {if(view.getDrawable.isInstanceOf[BitmapDrawable]) {
      val drawable = view.getDrawable.asInstanceOf[BitmapDrawable]
      drawable.getBitmap.recycle()
      view.setImageDrawable(null)
    }})
  }
}
