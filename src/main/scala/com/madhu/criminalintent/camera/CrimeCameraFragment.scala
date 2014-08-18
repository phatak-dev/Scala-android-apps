package com.madhu.criminalintent.camera

import android.support.v4.app.Fragment
import macroid.{Tweak, IdGeneration, Ui, Contexts}
import android.view._
import android.os.Bundle
import android.widget.{ProgressBar, Button, LinearLayout, FrameLayout}
import android.view.ViewGroup.LayoutParams._


import macroid.FullDsl._

import macroid.contrib.LpTweaks._
import android.hardware.Camera
import android.view.SurfaceHolder.Callback
import android.widget.FrameLayout.LayoutParams
import android.hardware.Camera.{PictureCallback, ShutterCallback}
import java.util.UUID
import java.io.FileOutputStream
import android.content.{Intent, Context}
import android.util.Log.d
import android.app.Activity


/**
 *
 * Created by madhu on 14/8/14.
 *
 */
object CrimeCameraFragment{
  val imageFileKey="com.madhu.criminalintent.camera.ImageFileName"
}

class CrimeCameraFragment extends Fragment
with Contexts[Fragment] with IdGeneration{
  var mCamera:Option[Camera] = _
  var surfaceView = slot[SurfaceView]
  var progressBar = slot[ProgressBar]

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
        val shutterCallBack = new ShutterCallback {
          override def onShutter(): Unit = {
            getUi { progressBar <~ show }
          }
        }

        val mJpegCallback = new PictureCallback {
          def onPictureTaken(data:Array[Byte],camera:Camera) = {
            val fileName = UUID.randomUUID().toString+".jpg"
            var success = true
            var os:FileOutputStream = null
            try {
              os = getActivity.openFileOutput(fileName,Context.MODE_PRIVATE)
              os.write(data)
            }
            catch {
              case e:Exception => d("$$$$$$$$$44","somethingwent wrong",e) ; success = false
            }
            finally {
              if(os!=null) os.close()
              success=true
            }
            if(success) {
              d("$$$$$$$$$$","saved fine")
              val intent = new Intent()
              intent.putExtra(CrimeCameraFragment.imageFileKey,fileName)
              getActivity.setResult(Activity.RESULT_OK,intent)
            }
            else {
              getActivity.setResult(Activity.RESULT_CANCELED)
            }
            getActivity.finish()
          }
        }

        mCamera.map(camera => {
          camera.takePicture(shutterCallBack,null,mJpegCallback)
        })

        Ui(true)
      }

      ) <~ matchParent ,
     w[ProgressBar] <~ Tweak((view:View) => {
       val layoutParams = new LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
       layoutParams.gravity = Gravity.CENTER
       view.setLayoutParams(layoutParams)
     }) <~ wire(progressBar) <~ hide
    ) <~ matchParent <~
    Tweak((view:View) => view.setClickable(true))

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
