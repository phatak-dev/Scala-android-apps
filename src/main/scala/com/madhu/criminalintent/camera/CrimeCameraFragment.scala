package com.madhu.criminalintent.camera

import android.support.v4.app.Fragment
import macroid.{IdGeneration, Ui, Contexts}
import android.view._
import android.os.Bundle
import android.widget.{Button, LinearLayout, FrameLayout}
import android.view.ViewGroup.LayoutParams._


import macroid.FullDsl._

import macroid.contrib.LpTweaks._
import android.hardware.Camera
import android.view.SurfaceHolder.Callback



/**
 *
 * Created by madhu on 14/8/14.
 */
class CrimeCameraFragment extends Fragment
with Contexts[Fragment] with IdGeneration{
  var mCamera:Option[Camera] = _
  var surfaceView = slot[SurfaceView]

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)

    val holder = surfaceView.get.getHolder
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

    holder.addCallback( new Callback {
      override def surfaceCreated(holder:SurfaceHolder) = {
        mCamera.map(camera => camera.setPreviewDisplay(holder))
      }

      override def surfaceDestroyed(holder:SurfaceHolder) = {
        mCamera.map(camera => camera.stopPreview())
      }

      override def surfaceChanged(holder:SurfaceHolder,format:Int, w:Int,h:Int) ={
        mCamera.map(camera => {
          val params = camera.getParameters
          /*val size:Size = null
          params.setPictureSize(size.width,size.height)*/
          camera.setParameters(params)
          camera.startPreview()
        })

      }
    })


  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = super.onCreateView(inflater, container, savedInstanceState)
    val layout = l[FrameLayout] (
      l[LinearLayout](
        w[SurfaceView] <~ wire(surfaceView) <~
        lp[LinearLayout](0 dp , MATCH_PARENT,1)
        ,
       w[Button] <~
       text("take") <~
       matchHeight <~

      On.click {
        getActivity.finish()
        Ui(true)
      }

      ) <~ matchParent
    ) <~ matchParent

    getUi(layout)
  }

  override def onResume(): Unit = {
    super.onResume()
    mCamera = Some(Camera.open(0))
  }

  override def onPause(): Unit = {
    super.onPause()
    mCamera.map(camera => camera.release())
    mCamera = None
  }
}
