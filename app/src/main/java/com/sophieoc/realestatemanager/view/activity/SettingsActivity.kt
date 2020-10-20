package com.sophieoc.realestatemanager.view.activity

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.RC_PERMISSION_PHOTO
import com.sophieoc.realestatemanager.utils.RC_SELECT_PHOTO
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SettingsActivity : BaseActivity() {
    private val userViewModel by viewModel<UserViewModel>()
    private lateinit var currentUser : UserWithProperties
    override fun getLayout()= Pair(R.layout.activity_settings, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.currentUser.observe(this, {
            //if (it != null) updateUI(it)
        })
        profile_picture.setOnClickListener { addPhoto() }
        username.setOnClickListener { editUsername() }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun updateUI(user: UserWithProperties) {
        username.text = user.user.username
        Glide.with(this)
                .load(user.user.urlPhoto)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_picture)
        currentUser = user
    }

    private fun editUsername() {
        edit_username_container.visibility = VISIBLE
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(edit_text_username, InputMethodManager.SHOW_IMPLICIT)
        save_username.setOnClickListener {
            if (edit_text_username.text.toString().isNotEmpty()) {
                currentUser.user.username = edit_text_username.text.toString()
                updateUser(currentUser)
                edit_username_container.visibility = GONE
                inputManager.hideSoftInputFromWindow(edit_text_username.windowToken, 0)
            }else
                edit_text_username.error = getString(R.string.empty_field)
        }
        cancel_username_update.setOnClickListener {
            edit_username_container.visibility = GONE
            edit_text_username.text.clear()
        }
    }

    private fun addPhoto() {
        if (ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission.READ_EXTERNAL_STORAGE), RC_PERMISSION_PHOTO)
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        this.startActivityForResult(intent, RC_SELECT_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                Toast.makeText(this, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage(data: Uri) {
        val uuid = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        if (Utils.isConnectionAvailable(this)) {
            imageRef.putFile(data)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val pathImage = uri.toString()
                            currentUser.user.urlPhoto = pathImage
                            updateUser(currentUser)
                        }
                    }
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(this, getString(R.string.load_picture_unable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    // todo: add progress bar
    private fun updateUser(user: UserWithProperties) {
        userViewModel.userUpdated.observe(this, {
            if (it != null) {
               // updateUI(it)
                Toast.makeText(this, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
            }
        })
        userViewModel.updateUser(user)
    }

    companion object{
        const val TAG = "SettingsActivityLog"
    }
}
