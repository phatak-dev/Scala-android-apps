package com.madhu.criminalintent.camera

import android.support.v4.app.{DialogFragment, Fragment}
import macroid.{Tweak, Contexts}
import android.os.Bundle
import android.view.{View, ViewGroup, LayoutInflater}
import android.widget.ImageView

import macroid.FullDsl._
import com.madhu.criminalintent.PictureUtils

/**
 * Created by madhu on 14/8/14.
 */
class ImageFragement extends DialogFragment
with Contexts[DialogFragment] {

  var imageViewSlot = slot[ImageView]

  override def onStop(): Unit = {
    super.onStop()
    PictureUtils.cleanUpImage(imageViewSlot)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)

    val imageView = w[ImageView] <~ Tweak{ (view:ImageView) => {
     val fullPath = getArguments.getSerializable(ImageFragement.EXTRA_IMAGE_PATH).asInstanceOf[String]
     val drawable = PictureUtils.getScaledDrawable(getActivity,fullPath)
     view.setImageDrawable(drawable)
    }
  } <~ wire(imageViewSlot)
  getUi(imageView)
}
}

object ImageFragement {
  val EXTRA_IMAGE_PATH="com.madhu.criminalintent.camera.imagepath"

  def newInstance(path:String):ImageFragement = {
    val bundle = new Bundle()
    bundle.putSerializable(EXTRA_IMAGE_PATH,path)

    val imageFragment = new ImageFragement
    imageFragment.setArguments(bundle)
    imageFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0)
    imageFragment
  }

}
