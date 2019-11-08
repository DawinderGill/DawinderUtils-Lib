package com.dawinderutilslib.pickers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.R
import com.dawinderutilslib.dialogs.ImagePickerDialog
import com.dawinderutilslib.listeners.OnDialogOptionSelected
import com.dawinderutilslib.listeners.OnImagePick
import java.io.File

@Suppress("UNUSED_PARAMETER")
object MyImagePicker {

    private var cameraClickPath = ""
    private const val REQUEST_CODE_GALLERY = 1001
    private const val REQUEST_CODE_CAMERA = 1002
    private lateinit var listener: OnImagePick

    object dialogListener : OnDialogOptionSelected {
        override fun onDialogOptionSelected(mContext: Context, isGallery: Boolean) {
            if (isGallery) getGalleryPermission(mContext)
            else getCameraPermission(mContext)
        }
    }

    @SuppressLint("NewApi")
    fun selectImage(mContext: Context, listener: OnImagePick) {
        this.listener = listener

        val dialog = ImagePickerDialog()
        dialog.setData(mContext, dialogListener)
        dialog.show((mContext as AppCompatActivity).supportFragmentManager, dialog.tag)
    }

    private fun getGalleryPermission(mContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_GALLERY
                )
            } else {
                gotoGallery(mContext)
            }
        } else {
            gotoGallery(mContext)
        }
    }

    private fun getCameraPermission(mContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_CODE_CAMERA
                )
            } else {
                gotoCamera(mContext)
            }
        } else {
            gotoCamera(mContext)
        }
    }

    fun onRequestPermissionsResult(
        mContext: Context,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoGallery(mContext)
            } else {
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.gallery_permission_required)
                )
            }
        }
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    gotoCamera(mContext)
                } else {
                    MyUtils.showToast(
                        mContext,
                        mContext.getString(R.string.permission_not_granted_message)
                    )
                }
            } else {
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.permission_not_granted_message)
                )
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun gotoGallery(mContext: Context) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        (mContext as AppCompatActivity).startActivityForResult(
            Intent.createChooser(intent, "Go To : "),
            REQUEST_CODE_GALLERY
        )
    }

    private fun gotoCamera(mContext: Context) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            val photoFile: File?
            try {
                photoFile = createImageFile(mContext)
                val photoURI =
                    FileProvider.getUriForFile(mContext, mContext.packageName, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                (mContext as AppCompatActivity).startActivityForResult(
                    takePictureIntent,
                    REQUEST_CODE_CAMERA
                )
            } catch (ex: Exception) {
                MyUtils.showLog("Ex : " + ex.message)
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.failed_to_capture_image)
                )
            }
        }
    }

    private fun createImageFile(mContext: Context): File {
        val imageFileName = "Image_" + System.currentTimeMillis() + "_"
        val storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".png", storageDir)
        cameraClickPath = image.absolutePath
        return image
    }

    fun onActivityResult(mContext: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val chosenImageUri = data.data
                try {
                    val realPath = MyUtils.getRealPathFromURI(mContext, chosenImageUri!!)!!
                    val imageFile = File(realPath)
                    if (imageFile.exists()) {
                        listener.onImagePick(imageFile.absolutePath)
                    } else {
                        MyUtils.showToast(
                            mContext,
                            mContext.getString(R.string.failed_to_fetch)
                        )
                    }
                } catch (e: Exception) {
                    MyUtils.showToast(mContext, mContext.getString(R.string.failed_to_fetch))
                }
            } else {
                MyUtils.showToast(mContext, mContext.getString(R.string.failed_to_fetch))
            }
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                listener.onImagePick(cameraClickPath)
            } else {
                MyUtils.showToast(
                    mContext,
                    mContext.getString(R.string.failed_to_capture_image)
                )
            }
        }
    }
}