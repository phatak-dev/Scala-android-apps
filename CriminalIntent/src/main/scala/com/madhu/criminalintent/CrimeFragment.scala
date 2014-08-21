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
import com.madhu.criminalintent.camera.ImageFragement
import android.provider.ContactsContract


// import macroid stuff

import macroid._
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
  val REQUEST_CONTACT = 2

  def newInstance(uuid: UUID): CrimeFragment = {
    val argsBundle = new Bundle()
    argsBundle.putSerializable(EXTRA_CRIME_ID, uuid)
    val fragment = new CrimeFragment()
    fragment.setArguments(argsBundle)
    fragment
  }
}




class CrimeFragment extends Fragment with CustomTweaks
with Contexts[Fragment] with IdGeneration {
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
      wire(imageViewSlot) <~
      On.click {
        if (!crime.imageFileName.isEmpty) {
          val fragmentManager = getActivity.getSupportFragmentManager
          val path = getActivity.getFileStreamPath(crime.imageFileName).getAbsolutePath
          ImageFragement.newInstance(path).show(fragmentManager, "image")
        }
        Ui(true)
      } <~ id(Id.imageViewId)


    val suspectButton = w[Button] <~
      text("Choose suspect") <~ On.click {
      val intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
      startActivityForResult(intent, CrimeFragment.REQUEST_CONTACT)
      Ui(true)
    } <~ id(Id.suspectButton)



    val reportButton = w[Button] <~
      text("Report crime") <~ On.click {
      val intent = new Intent(Intent.ACTION_SEND)
      intent.setType("text/plain")
      intent.putExtra(Intent.EXTRA_TEXT, Crime.getCrimeReport(crime))
      intent.putExtra(Intent.EXTRA_SUBJECT, "crime report")
      startActivity(Intent.createChooser(intent, "Send crime report"))
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
        imageView,
        suspectButton <~ margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp),
        reportButton <~ margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp)) <~
        vertical <~ matchWidth
    }

    val landscapeLayout = getUi {
      l[ScrollView](l[LinearLayout](
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
        imageView,
        l[LinearLayout](
          suspectButton <~ lp[LinearLayout](WRAP_CONTENT, WRAP_CONTENT, 1),
          reportButton <~ lp[LinearLayout](WRAP_CONTENT, WRAP_CONTENT, 1)
        ) <~ horizontal <~ margin(MATCH_PARENT, WRAP_CONTENT)(left = 16 dp, right = 16 dp)
      ) <~
        vertical <~ matchWidth) <~ matchParent
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
          showPhoto()
        }
        case CrimeFragment.REQUEST_CONTACT => {
          val uri = data.getData
          val cursor = getActivity.getContentResolver.query(
            uri, Array[String](ContactsContract.ContactsColumns.DISPLAY_NAME), null, null, null)
          if (cursor.getCount > 0) {
            cursor.moveToFirst()
            val suspect = cursor.getString(0)
            crime.suspect = suspect
            getActivity.setTitle(crime.suspect)
            getUi { getView.find[Button](Id.suspectButton) <~ text(crime.suspect) }
            d("$$$$$$$$$$$", s"suspect name is $suspect")
          }
          cursor.close()
        }
      }
    }
  }


  def showPhoto() = {
    d("$$$$$$$$$", "show photo called")
    getUi {getView.find[ImageView](Id.imageViewId) <~ Tweak{ imageView:ImageView => {
    val path = crime.imageFileName
    if (path != null && !path.isEmpty) {
      val fullPath = getActivity()
        .getFileStreamPath(path).getAbsolutePath()
      d("$$$$$$$$$", "setting image")
      imageView.setImageDrawable(PictureUtils.getScaledDrawable(getActivity, fullPath))
    }}}}
  }


override def onStart (): Unit = {
super.onStart ()
showPhoto ()
}

  override def onStop (): Unit = {
super.onStop ()
PictureUtils.cleanUpImage (imageViewSlot.get)
}
}