package com.sophieoc.realestatemanager.ui.editproperty

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.ActivityEditAddPropertyBinding
import com.sophieoc.realestatemanager.ui.BaseActivity
import com.sophieoc.realestatemanager.ui.property.PropertyUiState
import com.sophieoc.realestatemanager.ui.property.PropertyViewModel
import com.sophieoc.realestatemanager.util.AddPicturesFromPhoneUtil
import com.sophieoc.realestatemanager.util.PROPERTY_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
            getPropertyToEdit()
        else
            showFragments()

        //init ActivityResultLaunchers
        AddPicturesFragment.addPhotoUtil = AddPicturesFromPhoneUtil(this)
    }

    private fun showFragments() {
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

    private fun getPropertyToEdit() {
        val propertyId = intent?.extras?.get(PROPERTY_ID) as String
        lifecycleScope.launchWhenStarted {
            sharedViewModel.getPropertyById(propertyId).collect { propertyUiState ->
                when(propertyUiState){
                is PropertyUiState.Loading -> {/*Todo: show progressBar*/}
                    is PropertyUiState.Success -> {
                        sharedViewModel.property = propertyUiState.property
                        showFragments()
                    }
                    is PropertyUiState.Error -> { /*handleError(propertyUiState.exception) */}
                }
            }
        }
    }
}