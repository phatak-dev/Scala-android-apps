package com.madhu.criminalintent

import android.os.Bundle
import android.widget._
import android.widget.CompoundButton._
import android.view.ViewGroup.LayoutParams._
import android.view._

import android.support.v4.app._
import java.util.UUID
import android.text.TextWatcher
import android.text.Editable
import android.util.Log.d

import android.content.Intent
import com.madhu.criminalintent.camera.{CrimeCameraFragment, CrimeCameraActivity}
import android.content.pm.PackageManager
import android.app.Activity
import android.widget.ImageView.ScaleType
import com.madhu.criminalintent.camera.ImageFragement


// import macroid stuff

import macroid._
import macroid.Ui._
import macroid.FullDsl._
import macroid.contrib.LpTweaks._
import macroid.contrib._

trait CustomTweaks {
  def margin(width: Int, height: Int)(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0, all: Int = -1) = {
    val layout = new LinearLayout.LayoutParams(width, height)
    if (all >= 0) {
      layout.setMargins(all, all, all, all)
    } else {
      layout.setMargins(left, top, right, bottom)
    }
    Tweak[View](_.setLayoutParams(layout))
  }

  def listSeperator(implicit context: AppContext): Tweak[TextView] = Tweak {
    (textView: TextView) => {
      textView.setAllCaps(true)
    }
  } + lp[LinearLayout](
    MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER_VERTICAL) +
    TextTweaks.bold + TextTweaks.size(6 sp) +
    padding(left = 8 dp)

}

object CrimeFragment {
  val EXTRA_CRIME_ID = "com.madhu.criminalintent.CrimeFragment.ID"
  val REQUEST_PHOTO = 1

  def newInstance(uuid: UUID): CrimeFragment = {
    val argsBundle = new Bundle()
    argsBundle.putSerializable(EXTRA_CRIME_ID, uuid)
    val fragment = new CrimeFragment()
    fragment.setArguments(argsBundle)
    fragment
  }
}

class CrimeFragment extends Fragment with CustomTweaks
with Contexts[Fragment] {
  var crime: Crime = _
  var editText = slot[EditText]
  var checkBoxCrimeResolved = slot[CheckBox]
  var imageViewSlot = slot[ImageView]
  var photoButton = slot[ImageButton]

  override def onCreate(savedBundleInstance: Bundle) = {
    super.onCreate(savedBundleInstance)
    val crimeId = getArguments().getSerializable(
      CrimeFragment.EXTRA_CRIME_ID).asInstanceOf[UUID]
    crime = CrimeLab(getActivity).getCrime(crimeId).get
  }



  override def onCreateView(inflator: LayoutInflater,
                            parent: ViewGroup, savedBundleInstance: Bundle): View = {
    val title = w[TextView] <~
      text("Title") <~
      matchWidth <~
      listSeperator

    val crimeInfo = w[EditText] <~
      text(crime.mTitle) <~
      wire(editText) <~
      matchWidth <~
      margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp) <~
      Tweak {
        (view: EditText) => {
          view.addTextChangedListener(
            new TextWatcher() {
              override def onTextChanged(c: CharSequence,
                                         start: Int, before: Int, count: Int) = {
                crime.mTitle = c.toString()
              }

              override def beforeTextChanged(c: CharSequence,
                                             start: Int, count: Int, after: Int) = {

              }

              override def afterTextChanged(e: Editable) = {

              }
            })
        }
      }

    val details = w[TextView] <~
      text("Details") <~
      matchWidth <~
      listSeperator

    val dateButton = w[Button] <~
      text(crime.mDate.toString) <~
      disable

    val checkBox = w[CheckBox] <~
      text("Crime solved") <~
      wire(checkBoxCrimeResolved) <~
      Tweak {
        (view: CheckBox) => {
          view.setChecked(crime.solved)
        }
      } <~
      Tweak {
        (view: CheckBox) => {
          view.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {
              override def onCheckedChanged(
                                             buttonView: CompoundButton, isChecked: Boolean) {
                crime.solved = isChecked
              }
            })
        }
      }

    val imageButton = w[ImageButton] <~
      wrapContent <~
      Tweak((view: ImageButton) => view.setBackgroundResource(android.R.drawable.ic_menu_camera)) <~
      On.click {
        val intent = new Intent(getActivity, classOf[CrimeCameraActivity])
        startActivityForResult(intent, CrimeFragment.REQUEST_PHOTO)
        Ui(true)
      } <~ wire(photoButton)

    val imageView = w[ImageView] <~
      lp[LinearLayout](80 dp, 80 dp) <~
      Tweak {
        (view: ImageView) => {
          view.setScaleType(ScaleType.CENTER_INSIDE)
          view.setBackground(getResources.getDrawable(android.R.color.darker_gray))
          view.setCropToPadding(true)
          if(!crime.imageFileName.isEmpty) {
           val fullPath = getActivity.getFileStreamPath(crime.imageFileName).getAbsolutePath
           val drawable = PictureUtils.getScaledDrawable(getActivity,fullPath)
           view.setImageDrawable(drawable)
          }
        }
      } <~ wire(imageViewSlot)  <~
       On.click {
         if(!crime.imageFileName.isEmpty) {
           val fragmentManager = getActivity.getSupportFragmentManager
           val path = getActivity.getFileStreamPath(crime.imageFileName).getAbsolutePath
           ImageFragement.newInstance(path).show(fragmentManager,"image")
         }
         Ui(true)
       }

    //check do we have camera

    val pm = getActivity.getPackageManager
    val hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
      pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    if (!hasCamera) {
      getUi {
        imageButton <~ disable
      }
    }

    val portraitLayout = getUi {
      l[LinearLayout](
        title,
        crimeInfo,
        details,
        dateButton <~
          margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp),
        checkBox <~
          margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp),
        imageButton,
        imageView) <~
        vertical <~ matchWidth
    }

    val landscapeLayout = getUi {
      l[LinearLayout](
        title,
        crimeInfo,
        details,
        l[LinearLayout](
          dateButton <~
            lp[LinearLayout](0 dp, WRAP_CONTENT, 1.0f),
          checkBox <~
            lp[LinearLayout](0 dp, WRAP_CONTENT, 1.0f)
        ) <~
          horizontal <~ matchWidth,
        imageButton,
        imageView) <~
        vertical <~ matchWidth
    }

    if (NavUtils.getParentActivityName(getActivity) != null) {
      getActivity().getActionBar().setDisplayHomeAsUpEnabled(true)
    }

    setHasOptionsMenu(true)

    if (portrait) portraitLayout else landscapeLayout
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => {
        if (NavUtils.getParentActivityName(getActivity) != null) {
          NavUtils.navigateUpFromSameTask(getActivity)
        }
        true
      }
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onPause(): Unit = {
    super.onPause()
    CrimeLab(getActivity).saveCrimes()
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    d("the result code is", "" + resultCode)

    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      requestCode match {
        case CrimeFragment.REQUEST_PHOTO => {
          val fileName = data.getStringExtra(CrimeCameraFragment.imageFileKey)
          crime.imageFileName = fileName
          d("filename is got ", fileName)
        }
      }
    }
  }

  override def onStop(): Unit = {
    super.onStop()
    PictureUtils.cleanUpImage(imageViewSlot.get)
  }
}