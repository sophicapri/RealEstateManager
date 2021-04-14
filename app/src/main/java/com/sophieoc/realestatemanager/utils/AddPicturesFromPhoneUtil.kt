package com.sophieoc.realestatemanager.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPicturesFromPhoneUtil(
    private val activity: AppCompatActivity,
    var onActivityResultListener: OnActivityResultListener? = null
) {
    lateinit var bottomSheetDialog: CustomBottomSheetDialog
    lateinit var currentPhotoPath: String
    private val pickFromGalleryActivity: ActivityResultLauncher<Intent>
    private val takePicFromCameraActivity: ActivityResultLauncher<Intent>
    private val requestPermissionGallery: ActivityResultLauncher<String>
    private val requestPermissionCamera: ActivityResultLauncher<Array<String>>

    init {
        pickFromGalleryActivity =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                sendActivityResult(activityResult, RC_SELECT_PHOTO_GALLERY)
            }
        takePicFromCameraActivity =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                sendActivityResult(activityResult, RC_PHOTO_CAMERA)
            }
        requestPermissionGallery =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                handleRequestPermissionResult(isGranted, RC_PERMISSION_PHOTO_GALLERY)
            }
        requestPermissionCamera =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                var isGranted = true
                permissions.entries.forEach { permission ->
                    if (!permission.value) {
                        isGranted = false
                        return@forEach
                    }
                }
                handleRequestPermissionResult(isGranted, RC_PERMISSION_SAVE_FROM_CAMERA)
            }
    }

    fun addPhoto() {
        if (!::bottomSheetDialog.isInitialized)
            bottomSheetDialog = CustomBottomSheetDialog(
                activity,
                R.style.BottomSheetDialogBackground
            ).buildBottomSheetDialog()
        bottomSheetDialog.show()
        val addFromGalleryBtn = bottomSheetDialog.getBinding().addFromGalleryBtn
        val addFromCameraBtn = bottomSheetDialog.getBinding().addFromCameraBtn
        addFromGalleryBtn.setOnClickListener { addPhotoFromGallery() }
        addFromCameraBtn.setOnClickListener { addPhotoFromCamera() }
    }

    private fun addPhotoFromGallery() {
        if (ContextCompat.checkSelfPermission(
                activity,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionGallery.launch(READ_EXTERNAL_STORAGE)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickFromGalleryActivity.launch(intent)
    }

    private fun addPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(
                activity,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                activity,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionCamera.launch(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE))
            return
        }
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(
                        AddPicturesFragment.TAG,
                        "dispatchTakePictureIntent: Error occurred while creating the File" + ex.message
                    )
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        FILE_PROVIDER_AUTHORITIES,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePicFromCameraActivity.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun handleRequestPermissionResult(isGranted: Boolean, requestCode: Int) {
        bottomSheetDialog.dismiss()
        if (isGranted) {
            if (requestCode == RC_PERMISSION_PHOTO_GALLERY) {
                addPhotoFromGallery()
            } else if (requestCode == RC_PERMISSION_SAVE_FROM_CAMERA) {
                addPhotoFromCamera()
            }
        } else
            Log.d(AddPicturesFragment.TAG, "onRequestPermissionsResult: refused")
    }

    private fun sendActivityResult(activityResult: ActivityResult, requestCode: Int) {
        bottomSheetDialog.dismiss()
        onActivityResultListener?.onActivityResult(requestCode, activityResult)
    }

    interface OnActivityResultListener {
        fun onActivityResult(requestCode: Int, activityResult: ActivityResult)
    }
}