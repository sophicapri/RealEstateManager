package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.DialogCameraBinding
import com.sophieoc.realestatemanager.databinding.FragmentAddPicturesBinding
import com.sophieoc.realestatemanager.model.CustomBottomSheetDialog
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_add_pictures.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class AddPicturesFragment : Fragment(), PicturesAdapter.OnDeletePictureListener, PicturesAdapter.OnSetAsCoverListener {
    lateinit var binding: FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var rootActivity: EditOrAddPropertyActivity
    private lateinit var bottomSheetDialog: CustomBottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_pictures,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        if (rootActivity.activityRestarted) {
            binding.executePendingBindings()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        rootActivity.checkConnection()
        bindViews()
        configureRecyclerView()
    }

    private fun bindViews() {
        btn_add_picture.setOnClickListener { addPhoto() }
    }

    private fun addPhoto() {
        if (!::bottomSheetDialog.isInitialized)
            bottomSheetDialog = CustomBottomSheetDialog(rootActivity, R.style.BottomSheetDialogBackground).buildBottomSheetDialog()
        bottomSheetDialog.show()
        val addFromGalleryBtn = bottomSheetDialog.getBinding().addFromGalleryBtn
        val addFromCameraBtn = bottomSheetDialog.getBinding().addFromCameraBtn
        addFromGalleryBtn.setOnClickListener { addPhotoFromGallery() }
        addFromCameraBtn.setOnClickListener { addPhotoFromCamera() }
    }

    private fun addPhotoFromGallery() {
        if (context?.let { ActivityCompat.checkSelfPermission(it, READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO_GALLERY)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(intent, RC_SELECT_PHOTO_GALLERY)
    }

    private fun addPhotoFromCamera() {
        if (context?.let { ActivityCompat.checkSelfPermission(it, READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED
                || context?.let { ActivityCompat.checkSelfPermission(it, WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), RC_PERMISSION_SAVE_FROM_CAMERA)
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        this.startActivityForResult(intent, RC_PHOTO_CAMERA)
    }

    private fun configureRecyclerView() {
        recycler_view_pictures.setHasFixedSize(true)
        adapter = PicturesAdapter(this, this, rootActivity.propertyViewModel)
        recycler_view_pictures.adapter = adapter
    }

    override fun onDeleteClick(position: Int, photos: ArrayList<Photo>) {
        photos.removeAt(position)
        rootActivity.propertyViewModel.property.photos = photos
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bottomSheetDialog.dismiss()
        if (requestCode == RC_SELECT_PHOTO_GALLERY)
            handleResponseGallery(resultCode, data)
        else if (requestCode == RC_PHOTO_CAMERA)
            handleResponseCamera(resultCode, data)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION_PHOTO_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPhotoFromGallery()
        } else if (requestCode == RC_PERMISSION_SAVE_FROM_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            addPhotoFromCamera()
            Log.e(TAG, "onRequestPermissionsResult: ", )
        } else
            Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    private fun handleResponseGallery(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            data?.let { it -> it.data?.let { saveImage(it) } }
        else
            Toast.makeText(rootActivity, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun handleResponseCamera(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            data?.extras?.get(DATA_PATH).let { image ->
                showAlertDialog(image as Bitmap)
            }
        } else
            Toast.makeText(rootActivity, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun showAlertDialog(image: Bitmap) {
        val alertBuilder = AlertDialog.Builder(rootActivity, R.style.Dialog)
        val inflater = layoutInflater
        val bindingCameraDialog: DialogCameraBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_camera,
                null, false)
        alertBuilder.setView(bindingCameraDialog.root)
        val dialog = alertBuilder.create()
        bindingCameraDialog.image = image
        bindingCameraDialog.okBtnAddPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) saveImage(getImageUri(image))
            else saveImage(getImageUriOldSdk(image))
        }
        bindingCameraDialog.retakePhotoBtn.setOnClickListener { addPhotoFromCamera() }
        bindingCameraDialog.cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun saveImage(data: Uri) {
        progressBar.visibility = VISIBLE
        val arrayPhoto = ArrayList(rootActivity.propertyViewModel.property.photos)
        val uuid = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        if (Utils.isInternetAvailable(rootActivity)) {
            imageRef.putFile(data)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val pathImage = uri.toString()
                            arrayPhoto.add(Photo(pathImage, ""))
                            rootActivity.propertyViewModel.property.photos = arrayPhoto
                            adapter.notifyDataSetChanged()
                            progressBar.visibility = GONE
                        }
                    }
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(rootActivity, getString(R.string.load_picture_unable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    override fun onSetAsCoverClick(position: Int, photos: ArrayList<Photo>) {
        val photoToMove = photos[position]
        photos.remove(photoToMove)
        photos.add(0, photoToMove)
        rootActivity.propertyViewModel.property.photos = photos
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getImageUri(bitmap: Bitmap): Uri {
        val relativeLocation = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
        val resolver = rootActivity.contentResolver;
        var stream: OutputStream? = null
        var uri: Uri? = null
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null)
                throw IOException("Failed to create new MediaStore record.")
            stream = resolver.openOutputStream(uri)

            if (stream == null)
                throw IOException("Failed to get output stream.")

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream))
                throw  IOException("Failed to save bitmap.")

            return uri
        } catch (e: IOException) {
            if (uri != null)
                resolver.delete(uri, null, null)
            throw e
        } finally {
            stream?.close()
        }
    }

    private fun getImageUriOldSdk(image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(rootActivity.contentResolver, image, "Image", null)
        return Uri.parse(path)
    }

    companion object {
        const val TAG = "AddPictures"
    }
}