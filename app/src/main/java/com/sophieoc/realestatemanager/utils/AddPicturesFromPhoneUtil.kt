package com.sophieoc.realestatemanager.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddPicturesFromPhoneUtil(private val activity: AppCompatActivity, private val fragment: Fragment?) {
    lateinit var bottomSheetDialog: CustomBottomSheetDialog
    lateinit var currentPhotoPath: String

    fun addPhoto() {
        if (!::bottomSheetDialog.isInitialized)
            bottomSheetDialog = CustomBottomSheetDialog(activity, R.style.BottomSheetDialogBackground).buildBottomSheetDialog()
        bottomSheetDialog.show()
        val addFromGalleryBtn = bottomSheetDialog.getBinding().addFromGalleryBtn
        val addFromCameraBtn = bottomSheetDialog.getBinding().addFromCameraBtn
        addFromGalleryBtn.setOnClickListener { addPhotoFromGallery() }
        addFromCameraBtn.setOnClickListener { addPhotoFromCamera() }
    }

    fun addPhotoFromGallery() {
        if (ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (fragment != null)
                fragment.requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO_GALLERY)
            else
                ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO_GALLERY)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, RC_SELECT_PHOTO_GALLERY)
    }

    fun addPhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (fragment != null)
                fragment.requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), RC_PERMISSION_SAVE_FROM_CAMERA)
            else
                ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO_GALLERY)
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
                    Log.e(AddPicturesFragment.TAG, "dispatchTakePictureIntent: Error occurred while creating the File" + ex.message)
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            activity,
                            FILE_PROVIDER_AUTHORITIES,
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, RC_PHOTO_CAMERA)
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
}