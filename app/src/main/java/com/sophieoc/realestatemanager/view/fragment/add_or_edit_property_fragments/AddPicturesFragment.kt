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
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
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

class AddPicturesFragment : BaseFragment(), PicturesAdapter.OnDeletePictureListener, PicturesAdapter.OnSetAsCoverListener {
    lateinit var binding: FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var addPropertyActivity: EditOrAddPropertyActivity

    override fun getLayout(): Pair<Nothing?, View> = Pair(null, binding.root)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_pictures,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        if (addPropertyActivity.activityRestarted) {
            binding.executePendingBindings()
        }
        Log.d(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun onResume() {
        super.onResume()
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
        adapter = PicturesAdapter(this, this, addPropertyActivity.propertyViewModel)
        recycler_view_pictures.adapter = adapter
    }

    override fun onDeleteClick(position: Int, photos: ArrayList<Photo>) {
        photos.removeAt(position)
        addPropertyActivity.propertyViewModel.property.photos = photos
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
                Toast.makeText(mainContext, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(data: Uri) {
        val arrayPhoto = ArrayList(addPropertyActivity.propertyViewModel.property.photos)
        val uuid = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        if (Utils.isConnectionAvailable(mainContext)) {
            imageRef.putFile(data)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val pathImage = uri.toString()
                            arrayPhoto.add(Photo(pathImage, ""))
                            addPropertyActivity.propertyViewModel.property.photos = arrayPhoto
                            adapter.notifyDataSetChanged()
                        }
                    }
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(mainContext, getString(R.string.load_picture_unable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }

   /*    val arrayPhoto = ArrayList(addPropertyActivity.propertyViewModel.property.photos)
        data.path?.let {
            Log.d(TAG, "saveImage: path = $it")
            arrayPhoto.add(Photo(data, ""))
            addPropertyActivity.propertyViewModel.property.photos = arrayPhoto
        }

    */
    }

    companion object {
        const val TAG = "AddPictures"
    }

    override fun onSetAsCoverClick(position: Int, photos: ArrayList<Photo>) {
        val photoToMove = photos[position]
        photos.remove(photoToMove)
        photos.add(0, photoToMove)
        addPropertyActivity.propertyViewModel.property.photos = photos
    }
}