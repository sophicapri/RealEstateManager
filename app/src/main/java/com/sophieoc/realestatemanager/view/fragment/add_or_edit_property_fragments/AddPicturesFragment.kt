package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddPicturesBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddPicturesFragment : Fragment(), PicturesAdapter.OnDeletePictureListener, PicturesAdapter.OnSetAsCoverListener {
    lateinit var binding: FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var addPhotoUtil : AddPicturesFromPhoneUtil
    private val sharedViewModel: PropertyViewModel by lazy { requireActivity().getViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_pictures,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        if (EditOrAddPropertyActivity.activityRestarted) {
            binding.executePendingBindings()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        bindViews()
        configureRecyclerView()
    }

    private fun bindViews() {
        binding.btnAddPicture.setOnClickListener {
            addPhotoUtil = AddPicturesFromPhoneUtil(requireActivity() as EditOrAddPropertyActivity, this)
            addPhotoUtil.addPhoto()
        }
    }

    private fun configureRecyclerView() {
        adapter = PicturesAdapter(this, this, sharedViewModel)
        binding.apply {
            recyclerViewPictures.setHasFixedSize(true)
            recyclerViewPictures.adapter = adapter
        }
    }

    override fun onDeleteClick(position: Int, photos: ArrayList<Photo>) {
        photos.removeAt(position)
        sharedViewModel.property.photos = photos
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        addPhotoUtil.bottomSheetDialog.dismiss()
        if (requestCode == RC_SELECT_PHOTO_GALLERY)
            handleResponseGallery(resultCode, data)
        else if (requestCode == RC_PHOTO_CAMERA)
            handleResponseCamera(resultCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        addPhotoUtil.bottomSheetDialog.dismiss()
        if (requestCode == RC_PERMISSION_PHOTO_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPhotoUtil.addPhotoFromGallery()
        } else if (requestCode == RC_PERMISSION_SAVE_FROM_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            addPhotoUtil.addPhotoFromCamera()
        } else
            Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    private fun handleResponseGallery(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            data?.let { it -> it.data?.let { saveImage(it) } }
        else
            Toast.makeText(requireContext(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun handleResponseCamera(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            val f = File(addPhotoUtil.currentPhotoPath)
            saveImage(Uri.fromFile(f))
        } else
            Toast.makeText(requireContext(), getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun saveImage(data: Uri) {
        binding.progressBar.visibility = VISIBLE
        val arrayPhoto = ArrayList(sharedViewModel.property.photos)
        val uuid = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        if (Utils.isInternetAvailable(requireContext())) {
            imageRef.putFile(data)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val pathImage = uri.toString()
                            arrayPhoto.add(Photo(pathImage, ""))
                            sharedViewModel.property.photos = arrayPhoto
                            adapter.notifyDataSetChanged()
                            binding.progressBar.visibility = GONE
                        }
                    }
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(requireContext(), getString(R.string.load_picture_unable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    override fun onSetAsCoverClick(position: Int, photos: ArrayList<Photo>) {
        val photoToMove = photos[position]
        photos.remove(photoToMove)
        photos.add(0, photoToMove)
        sharedViewModel.property.photos = photos
    }

    companion object {
        const val TAG = "AddPictures"
    }
}