package com.sophieoc.realestatemanager.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.databinding.DataBindingUtil
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivitySettingsBinding
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

class SettingsActivity : BaseActivity() {
    private val userViewModel by viewModel<UserViewModel>()
    private lateinit var currentUser: UserWithProperties
    private lateinit var binding: ActivitySettingsBinding
    private var dataChanged = false
    private lateinit var addPhotoUtil : AddPicturesFromPhoneUtil

    override fun getLayout() = Pair(null, binding.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this
        userViewModel.userUpdated.observe(this, {
            if (it != null) {
                binding.progressBar.visibility = GONE
                if (dataChanged) {
                    Toast.makeText(this, getString(R.string.changes_saved), Toast.LENGTH_LONG).show()
                    dataChanged = false
                }
                currentUser = it
            }
        })
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        super.onCreate(savedInstanceState)
    }

    fun editUsername(view: View) {
        checkConnection()
        if (PreferenceHelper.internetAvailable) {
            binding.editUsernameContainer.visibility = VISIBLE
            showSoftKeyboard(binding.editTextUsername)
            binding.editTextUsername.requestFocus()
            binding.editTextUsername.setSelection(currentUser.user.username.length)
            binding.editTextUsername.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    saveUsername(null)
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        } else
            Toast.makeText(this, getString(R.string.cannot_edit_username_offline), LENGTH_LONG).show()
    }

    fun saveUsername(view: View?) {
        binding.apply {
            if (editTextUsername.text.toString().isNotEmpty()) {
                progressBar.visibility = VISIBLE
                currentUser.user.username = editTextUsername.text.toString()
                editUsernameContainer.visibility = GONE
                hideSoftKeyboard(editTextUsername)
                dataChanged = true
                userViewModel?.updateUser(currentUser)
            } else
                editTextUsername.error = getString(R.string.empty_field)
        }
    }

    fun cancelUsernameEdit(view: View) {
        binding.editUsernameContainer.visibility = GONE
    }

    fun addPhoto(view: View?) {
        checkConnection()
        if (PreferenceHelper.internetAvailable) {
            addPhotoUtil = AddPicturesFromPhoneUtil(this, null)
            addPhotoUtil.addPhoto()
        } else
            Toast.makeText(this, getString(R.string.cannot_change_photo_offline), LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addPhotoUtil.bottomSheetDialog.dismiss()
        if (requestCode == RC_SELECT_PHOTO_GALLERY)
            handleResponseGallery(resultCode, data)
        else if (requestCode == RC_PHOTO_CAMERA)
            handleResponseCamera(resultCode)
    }

    //TODO: Change to registerActivityForResult()
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        addPhotoUtil.bottomSheetDialog.dismiss()
        if (requestCode == RC_PERMISSION_PHOTO_GALLERY && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addPhotoUtil.addPhotoFromGallery()
        } else if (requestCode == RC_PERMISSION_SAVE_FROM_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            addPhotoUtil.addPhotoFromCamera()
        } else
            Log.d(AddPicturesFragment.TAG, "onRequestPermissionsResult: refused")
    }

    private fun handleResponseGallery(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            data?.let { it -> it.data?.let { saveImage(it) } }
        else
            Toast.makeText(this, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun handleResponseCamera(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            val f = File(addPhotoUtil.currentPhotoPath)
            saveImage(Uri.fromFile(f))
        } else
            Toast.makeText(this, getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
    }

    private fun saveImage(data: Uri) {
        val uuid = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference(uuid)
        if (Utils.isInternetAvailable(this)) {
            binding.progressBar.visibility = VISIBLE
            imageRef.putFile(data)
                    .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri: Uri ->
                            val pathImage = uri.toString()
                            currentUser.user.urlPhoto = pathImage
                            dataChanged = true
                            userViewModel.updateUser(currentUser)
                        }
                    }
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(this, getString(R.string.load_picture_unable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        const val TAG = "SettingsActivityLog"
    }
}
