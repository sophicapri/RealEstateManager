package com.sophieoc.realestatemanager.presentation.ui.editproperty

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivityEditAddPropertyBinding
import com.sophieoc.realestatemanager.presentation.BaseActivity
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel
import com.sophieoc.realestatemanager.utils.AddPicturesFromPhoneUtil
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAddPropertyActivity : BaseActivity() {
    lateinit var binding: ActivityEditAddPropertyBinding
    private val sharedViewModel by viewModels<PropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding.apply {
            propertyViewModel = sharedViewModel
        }
        if (intent.extras != null && intent!!.extras!!.containsKey(PROPERTY_ID))
            getProperty()
        else
            launchFragments()

        //init ActivityResultLaunchers
        AddPicturesFragment.addPhotoUtil = AddPicturesFromPhoneUtil(this)
    }

    private fun launchFragments() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame_add_property, EditAddPropertyFragment(),
                EditAddPropertyFragment().javaClass.simpleName
            )
            .commit()
    }

/*
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        activityRestarted = true
        supportFragmentManager.find(AddAddressFragment()::class.java.simpleName)
            ?.let { fragmentAddress = it as AddAddressFragment }
        supportFragmentManager.findFragmentByTag(AddPropertyInfoFragment()::class.java.simpleName)
            ?.let { fragmentPropertyInfo = it as AddPropertyInfoFragment }
        supportFragmentManager.findFragmentByTag(AddPicturesFragment()::class.java.simpleName)
            ?.let { fragmentPictures = it as AddPicturesFragment }
    }
*/

    override fun getLayout(): View {
        binding = ActivityEditAddPropertyBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun getProperty() {
        val propertyId = intent?.extras?.get(PROPERTY_ID) as String
        sharedViewModel.getPropertyById(propertyId).observe(this, { property ->
            property?.let {
                sharedViewModel.property = it
                launchFragments()
                Log.d(TAG, "bindViews: ${it.description}")
            }
        })
    }
}