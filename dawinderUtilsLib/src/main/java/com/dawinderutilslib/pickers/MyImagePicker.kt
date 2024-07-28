package com.dawinderutilslib.pickers

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dawinderutilslib.MyUtils
import com.dawinderutilslib.R
import com.dawinderutilslib.dialogs.ImagePickerDialog
import com.dawinderutilslib.listeners.OnDialogOptionSelected
import com.dawinderutilslib.listeners.OnImagePick
import java.io.File

object MyImagePicker {

    private var cameraClickPath = ""
    private const val REQUEST_CODE_GALLERY = 1001
    private const val REQUEST_CODE_CAMERA = 1002
    private var REQUEST_CODE_SELECTED = 0
    private lateinit var listener: OnImagePick
    private var isGallery: Boolean = true
    private var isCamera: Boolean = true
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    object dialogListener : OnDialogOptionSelected {
        override fun onDialogOptionSelected(mContext: Context, isGallery: Boolean) {
            if (isGallery) getGalleryPermission(mContext)
            else getCameraPermission(mContext)
        }
    }

    fun disableGallery(): MyImagePicker {
        isGallery = false
        return this
    }

    fun disableCamera(): MyImagePicker {
        isCamera = false
        return this
    }

    @SuppressLint("NewApi")
    fun selectImage(
        mContext: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
        listener: OnImagePick
    ) {
        this.listener = listener
        this.activityResultLauncher = activityResultLauncher
        if ((isCamera && isGallery) || (!isCamera && !isGallery)) {
            val dialog = ImagePickerDialog()
            dialog.setData(mContext, dialogListener)
            dialog.show((mContext as AppCompatActivity).supportFragmentManager, dialog.tag)
        } else if (isCamera) {
            gotoCamera(mContext)
        } else {
            gotoGallery()
        }
    }

    private fun getGalleryPermission(mContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED),
                    REQUEST_CODE_GALLERY
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES), REQUEST_CODE_GALLERY
                )
            } else {
                (mContext as AppCompatActivity).requestPermissions(
                    arrayOf(READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_GALLERY
                )
            }
        } else {
            gotoGallery()
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
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                gotoGallery()
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
    private fun gotoGallery() {
        REQUEST_CODE_SELECTED = REQUEST_CODE_GALLERY
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activityResultLauncher.launch(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun gotoCamera(mContext: Context) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            val photoFile: File?
            try {
                REQUEST_CODE_SELECTED = REQUEST_CODE_CAMERA
                photoFile = createImageFile(mContext)
                val photoURI =
                    FileProvider.getUriForFile(mContext, mContext.packageName, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activityResultLauncher.launch(takePictureIntent)
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

    fun onActivityResult(mContext: Context, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE_SELECTED == REQUEST_CODE_GALLERY) {
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

        if (REQUEST_CODE_SELECTED == REQUEST_CODE_CAMERA) {
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