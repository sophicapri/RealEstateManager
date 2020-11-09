package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddPicturesBinding
import com.sophieoc.realestatemanager.model.CustomBottomSheetDialog
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_add_pictures.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddPicturesFragment : Fragment(), PicturesAdapter.OnDeletePictureListener, PicturesAdapter.OnSetAsCoverListener {
    lateinit var binding: FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var rootActivity: EditOrAddPropertyActivity
    private lateinit var bottomSheetDialog: CustomBottomSheetDialog
    private lateinit var currentPhotoPath: String

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
        dispatchTakePictureIntent()
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
            handleResponseCamera(resultCode)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION_PHOTO_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPhotoFromGallery()
        } else if (requestCode == RC_PERMISSION_SAVE_FROM_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            addPhotoFromCamera()
            Log.e(TAG, "onRequestPermissionsResult: ")
        } else
            Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    private fun handleResponseGallery(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            data?.let { it -> it.data?.let { saveImage(it) } }
        else
            Toast.makeText(rootActivity, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun handleResponseCamera(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            val f = File(currentPhotoPath)
            saveImage(Uri.fromFile(f))
        } else
            Toast.makeText(rootActivity, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(rootActivity.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(TAG, "dispatchTakePictureIntent: Error occurred while creating the File" + ex.message)
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            rootActivity,
                            FILE_PROVIDER_AUTHORITIES,
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RC_PHOTO_CAMERA)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = rootActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
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

    companion object {
        const val TAG = "AddPictures"
    }
}