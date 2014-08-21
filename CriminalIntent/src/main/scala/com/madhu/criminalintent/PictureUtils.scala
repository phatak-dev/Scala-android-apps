package com.madhu.criminalintent

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.BitmapFactory
import android.widget.ImageView

/**
 * Created by madhu on 21/8/14.
 */
object PictureUtils {

  def getScaledDrawable(a:Activity,path:String):BitmapDrawable = {

    val display = a.getWindowManager().getDefaultDisplay()
    val destWidth = display.getWidth()
    val destHeight = display.getHeight()
    // Read in the dimensions of the image on disk
    var options = new BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    val  srcWidth = options.outWidth
    val srcHeight = options.outHeight
    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
      if (srcWidth > srcHeight) {
        inSampleSize = Math.round(srcHeight / destHeight);
      } else {
        inSampleSize = Math.round(srcWidth / destWidth);
      }
    }
    options = new BitmapFactory.Options()
    options.inSampleSize = inSampleSize

   val  bitmap = BitmapFactory.decodeFile(path, options)
   new BitmapDrawable(a.getResources(), bitmap)

  }

  def cleanUpImage(imageView:ImageView) = {
    if(imageView.getDrawable.isInstanceOf[BitmapDrawable]) {
      val b = imageView.getDrawable.asInstanceOf[BitmapDrawable]
      b.getBitmap().recycle()
      imageView.setImageDrawable(null)
    }


  }


}
