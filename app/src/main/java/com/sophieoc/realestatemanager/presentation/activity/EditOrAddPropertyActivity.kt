package com.sophieoc.realestatemanager.presentation.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivityEditAddPropertyBinding
import com.sophieoc.realestatemanager.notification.NotificationHelper
import com.sophieoc.realestatemanager.presentation.fragment.add_or_edit_property_fragments.AddAddressFragment
import com.sophieoc.realestatemanager.presentation.fragment.add_or_edit_property_fragments.AddPicturesFragment
import com.sophieoc.realestatemanager.presentation.fragment.add_or_edit_property_fragments.AddPropertyInfoFragment
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class EditOrAddPropertyActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    lateinit var binding: ActivityEditAddPropertyBinding
    private val sharedViewModel by viewModels<PropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditAddPropertyBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        bindViews()

        //init ActivityResultLaunchers
        fragmentPictures.addPhotoUtil = AddPicturesFromPhoneUtil(this)
    }

    override fun getLayout() = binding.root

    private fun bindViews() {
        binding.apply {
            propertyViewModel = sharedViewModel
            activity = this@EditOrAddPropertyActivity
            intent.extras?.let {
                titleEditCreate.text = getString(R.string.edit_property_title)
            }
            if (intent.extras == null) titleEditCreate.text = getString(R.string.add_property_title)
            toolbar.setNavigationOnClickListener { onBackPressed() }
            bottomNavigationBar.setOnNavigationItemSelectedListener(this@EditOrAddPropertyActivity)
            bottomNavigationBar.setBackgroundColor(
                ContextCompat.getColor(
                    this@EditOrAddPropertyActivity,
                    R.color.translucent_scrim_top_center
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras != null && (intent.extras as Bundle).containsKey(PROPERTY_ID))
            getPropertyId(intent.extras as Bundle)
        else
            addFragmentsToActivity()
    }

    private fun getPropertyId(extras: Bundle) {
        val propertyId = extras.get(PROPERTY_ID) as String
        sharedViewModel.getPropertyById(propertyId).observe(this, { property ->
            property?.let {
                sharedViewModel.property = it
                addFragmentsToActivity()
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        activityRestarted = true
        supportFragmentManager.findFragmentByTag(AddAddressFragment()::class.java.simpleName)
            ?.let { fragmentAddress = it as AddAddressFragment }
        supportFragmentManager.findFragmentByTag(AddPropertyInfoFragment()::class.java.simpleName)
            ?.let { fragmentPropertyInfo = it as AddPropertyInfoFragment }
        supportFragmentManager.findFragmentByTag(AddPicturesFragment()::class.java.simpleName)
            ?.let { fragmentPictures = it as AddPicturesFragment }
    }

    private fun addFragmentsToActivity() {
        if (!activityRestarted && !fragmentAddress.isAdded) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(
                R.id.frame_add_property,
                fragmentAddress,
                fragmentAddress::class.java.simpleName
            )
                .add(
                    R.id.frame_add_property,
                    fragmentPropertyInfo,
                    fragmentPropertyInfo::class.java.simpleName
                )
                .add(
                    R.id.frame_add_property,
                    fragmentPictures,
                    fragmentPictures::class.java.simpleName
                )
                .hide(fragmentPropertyInfo).hide(fragmentPictures).commit()
        }
    }

    override fun onRestart() {
        super.onRestart()
        activityRestarted = true
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        when {
            fragmentAddress.isVisible -> fragmentTransaction.hide(fragmentAddress)
            fragmentPropertyInfo.isVisible -> fragmentTransaction.hide(fragmentPropertyInfo)
            fragmentPictures.isVisible -> fragmentTransaction.hide(fragmentPictures)
        }
        if (fragment.isAdded)
            fragmentTransaction.show(fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.address_page -> showFragment(fragmentAddress)
            R.id.property_info_page -> showFragment(fragmentPropertyInfo)
            R.id.pictures_page -> showFragment(fragmentPictures)
        }
        return true
    }

    fun saveChanges() {
        if (Utils.isInternetAvailable(this)) {
            PreferenceHelper.internetAvailable = false
            checkDates()
            if (checkInputs()) {
                binding.progressBar.visibility = VISIBLE
                sharedViewModel.property.userId = PreferenceHelper.currentUserId
                saveProperty()
            } else
                showAlertDialog()
        } else {
            Toast.makeText(this, getString(R.string.cant_save_property), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun checkDates() {
        if (sharedViewModel.property.availability == PropertyAvailability.AVAILABLE)
            sharedViewModel.property.dateSold = null
        else
            sharedViewModel.property.dateOnMarket = null
    }

    private fun saveProperty() {
        sharedViewModel.upsertProperty()
        sharedViewModel.propertySaved.observe(this, {
            it?.let {
                onBackPressed()
                binding.progressBar.visibility = GONE
                displayNotification()
            }
        })
    }

    private fun checkInputs(): Boolean {
        return fragmentAddress.checkAddressPage()
            .and(fragmentPropertyInfo.checkMainInfoPage())
    }

    private fun showAlertDialog() {
        var message = getString(R.string.fill_in_missing_fields)
        if (emptyFieldsInAddress) {
            message += getString(R.string.in_address_page)
        }
        if (emptyFieldsInMainInfo) {
            if (emptyFieldsInAddress)
                message += getString(R.string.and)
            message += getString(R.string.in_main_info_page)
        }
        message += "."

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.forgot_info_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_btn)) { dialog, _ -> dialog.dismiss() }
            .setOnDismissListener(this)
            .create().show()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        emptyFieldsInAddress = false
        emptyFieldsInMainInfo = false
    }

    private fun displayNotification() {
        val notificationHelper = NotificationHelper(this)
        val nb: NotificationCompat.Builder = notificationHelper
            .getChannelNotification(getString(R.string.property_saved_successful))
        with(NotificationManagerCompat.from(this)) {
            notify(NotificationHelper.NOTIFICATION_ID, nb.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRestarted = false
    }

    companion object {
        const val TAG = "AddPropertyActivity"
        var activityRestarted = false
        private var fragmentAddress = AddAddressFragment()
        var fragmentPropertyInfo = AddPropertyInfoFragment()
        var fragmentPictures = AddPicturesFragment()
        var emptyFieldsInAddress = false
        var emptyFieldsInMainInfo = false
    }
}