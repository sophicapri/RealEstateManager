package com.sophieoc.realestatemanager.view.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.ActivityEditAddPropertyBinding
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.notification.NotificationHelper
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddAddressFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPropertyInfoFragment
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class EditOrAddPropertyActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    lateinit var binding: ActivityEditAddPropertyBinding
    private val sharedViewModel by viewModel<PropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditAddPropertyBinding.inflate(layoutInflater)
        bindViews()
        intent.extras?.let {
            getPropertyId(it)
        }
        //init ActivityResultLaunchers
        fragmentPictures.addPhotoUtil = AddPicturesFromPhoneUtil(this)
        super.onCreate(savedInstanceState)
    }

    override fun getLayout() = binding.root

    private fun bindViews() {
        binding.apply {
            propertyViewModel = propertyViewModel
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

    private fun getPropertyId(extras: Bundle) {
        if (extras.containsKey(PROPERTY_ID)) {
            val propertyId = extras.get(PROPERTY_ID) as String
            getProperty(propertyId)
        }
    }

    private fun getProperty(propertyId: String) {
        sharedViewModel.getPropertyById(propertyId).observe(this, {
            it?.let {
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

    fun saveChanges(view: View) {
        if (Utils.isInternetAvailable(this)) {
            PreferenceHelper.internetAvailable = false
            checkDates()
            if (checkInputs()) {
                binding.progressBar.visibility = VISIBLE
                sharedViewModel.property.userId = PreferenceHelper.currentUserId
                updatePointOfInterestsAndSave(sharedViewModel.property)
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

    private fun updatePointOfInterestsAndSave(property: Property) {
        val strLocation = property.address.toLatLng(this).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(this).latitude
        location.longitude = property.address.toLatLng(this).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            sharedViewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
                placeDetailList?.let {
                    if (placeDetailList.isNotEmpty()) {
                        val listPointOfInterest = ArrayList<PointOfInterest>()
                        placeDetailList.forEachIndexed { index, placeDetails ->
                            val pointOfInterest = setPointOfInterest(placeDetails, location)
                            listPointOfInterest.add(pointOfInterest)
                            if ((index + 1) == placeDetailList.size) {
                                property.pointOfInterests = listPointOfInterest
                                saveProperty()
                            }
                        }
                    }
                }
                if (placeDetailList == null)
                    Log.d(TAG, "setPointOfInterestsAndSave: list == null")
            })
        } else {
            Log.d(TAG, "setPointOfInterestsAndSave: latLng not found")
            showAddressErrorDialog()
        }
    }

    private fun setPointOfInterest(
        placeDetails: PlaceDetails,
        location: Location
    ): PointOfInterest {
        val position: Int
        val pointOfInterest = PointOfInterest()
        pointOfInterest.name = placeDetails.name.toString()
        pointOfInterest.address = placeDetails.vicinity.toString()
        pointOfInterest.distance = placeDetails.getDistanceFrom(location)
        val placeTypes = placeDetails.types
        if (placeTypes != null) {
            position = if (placeTypes[0] != POINT_OF_INTEREST)
                0
            else
                1
            pointOfInterest.type = placeTypes[position].capitalize(Locale.ROOT).replace('_', ' ')
            if (placeTypes.contains(PARK))
                pointOfInterest.mainType = PARK
            if (placeTypes.contains(SCHOOL))
                pointOfInterest.mainType = SCHOOL
            if (placeTypes.contains(STORE))
                pointOfInterest.mainType = STORE
        }
        return pointOfInterest
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

    private fun showAddressErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_cant_locate_address))
            .setMessage(getString(R.string.address_not_found))
            .setPositiveButton(getString(R.string.edit_address_btn)) { _, _ ->
                showFragment(
                    fragmentAddress
                )
            }
            .setNegativeButton(getString(R.string.ignore_btn)) { dialog, _ -> dialog.dismiss() }
            .create().show()
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