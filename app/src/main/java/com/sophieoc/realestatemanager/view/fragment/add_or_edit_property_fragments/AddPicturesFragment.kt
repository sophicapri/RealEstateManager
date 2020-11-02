package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddPicturesBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.RC_PERMISSION_PHOTO
import com.sophieoc.realestatemanager.utils.RC_SELECT_PHOTO
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_add_pictures.*
import java.util.*
import kotlin.collections.ArrayList

class AddPicturesFragment : Fragment(), PicturesAdapter.OnDeletePictureListener, PicturesAdapter.OnSetAsCoverListener {
    lateinit var binding: FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var rootActivity: EditOrAddPropertyActivity

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
        if (context?.let { ActivityCompat.checkSelfPermission(it, READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(intent, RC_SELECT_PHOTO)
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
        handleResponse(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION_PHOTO && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPhoto()
        } else
            Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                data?.let { it -> it.data?.let { saveImage(it) } }
            } else {
                Toast.makeText(rootActivity, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
            }
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