package com.sophieoc.realestatemanager.presentation.ui.editproperty

import android.os.Bundle
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
    private var editPropertyFragment = EditAddPropertyFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                R.id.frame_add_property, editPropertyFragment,
                editPropertyFragment.javaClass.simpleName
            )
            .commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        supportFragmentManager.findFragmentByTag(editPropertyFragment.javaClass.simpleName)
            ?.let { editPropertyFragment = it as EditAddPropertyFragment }
    }

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
            }
        })
    }
}