package com.sophieoc.realestatemanager.view.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import kotlinx.android.synthetic.main.activity_edit_add_property.*
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_add_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class EditOrAddPropertyActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    private var fragmentAddress: Fragment = AddAddressFragment()
    private var fragmentPropertyInfo: Fragment = AddPropertyInfoFragment()
    private var fragmentPictures: Fragment = AddPicturesFragment()
    var activityRestarted = false
    var emptyFieldsInAddress = false
    var emptyFieldsInMainInfo = false
    lateinit var binding: ActivityEditAddPropertyBinding
    val propertyViewModel by viewModel<PropertyViewModel>()


    override fun getLayout() = Pair(null, binding.root)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_add_property)
        binding.propertyViewModel = propertyViewModel
        intent.extras?.let {
            title_edit_create.text = getString(R.string.edit_property_title)
        }
        if (intent.extras == null) title_edit_create.text = getString(R.string.add_property_title)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        bottom_navigation_bar.setOnNavigationItemSelectedListener(this)
        super.onCreate(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: ")
        activityRestarted = true
        supportFragmentManager.findFragmentByTag(AddAddressFragment()::class.java.simpleName)?.let { fragmentAddress = it }
        supportFragmentManager.findFragmentByTag(AddPropertyInfoFragment()::class.java.simpleName)?.let { fragmentPropertyInfo = it }
        supportFragmentManager.findFragmentByTag(AddPicturesFragment()::class.java.simpleName)?.let { fragmentPictures = it }

    }

    override fun onResume() {
        super.onResume()
        if (!activityRestarted) {
            showFragment(fragmentAddress)
        }
    }

    private fun showFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        when {
            fragmentAddress.isVisible -> fragmentTransaction.hide(fragmentAddress)
            fragmentPropertyInfo.isVisible -> fragmentTransaction.hide(fragmentPropertyInfo)
            fragmentPictures.isVisible -> fragmentTransaction.hide(fragmentPictures)
        }
        if (fragment.isAdded) {
            fragmentTransaction.show(fragment).commit()
        } else {
            fragmentTransaction.add(R.id.frame_add_property, fragment, fragment::class.java.simpleName).commit()
        }
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
        val property = propertyViewModel.property
        setDates(property)
        if (checkInputs(property)) {
            updatePointOfInterestsAndSave(property)
        } else
            showAlertDialog()
    }

    private fun setDates(property: Property) {
        if (property.dateOnMarket == null) property.dateOnMarket = Date()
        if (property.availability == PropertyAvailability.SOLD && property.dateSold == null) property.dateSold = Date()
        if (property.dateSold != null && property.availability == PropertyAvailability.AVAILABLE) property.dateSold = null
    }

    private fun updatePointOfInterestsAndSave(property: Property) {
        val strLocation = property.address.toLatLng(this).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(this).latitude
        location.longitude = property.address.toLatLng(this).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            viewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
                placeDetailList?.let {
                    if (placeDetailList.isNotEmpty()) {
                        val listPointOfInterest = ArrayList<PointOfInterest>()
                        placeDetailList.forEachIndexed { index, placeDetails ->
                            val pointOfInterest = setPointOfInterest(placeDetails, location)
                            listPointOfInterest.add(pointOfInterest)
                            if ((index + 1) == placeDetailList.size) {
                                property.pointOfInterests = listPointOfInterest
                                saveProperty(property)
                            }
                        }
                    }
                }
                if (placeDetailList == null)
                    Log.d(TAG, "setPointOfInterestsAndSave: list == null")
            })
        } else
            Log.d(TAG, "setPointOfInterestsAndSave: latlng not found")
    }

    private fun setPointOfInterest(placeDetails: PlaceDetails, location: Location): PointOfInterest {
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

    private fun saveProperty(property: Property) {
        propertyViewModel.upsertProperty()
        propertyViewModel.propertySaved.observe(this, Observer {
            it?.let {
                //TODO : Add progress bar
                displayNotification()
                onBackPressed()
            }
        })
    }

    fun checkInputs(property: Property): Boolean {
        var valid = true
        emptyFieldsInAddress = false
        emptyFieldsInMainInfo = false
        if (property.address.streetNumber.isEmpty() || property.address.streetName.isEmpty() ||
                property.address.city.isEmpty() || property.address.postalCode.isEmpty()) {
            if (property.address.streetNumber.isEmpty())
                street_nbr_input?.error = getString(R.string.empty_field)
            if (property.address.streetName.isEmpty())
                street_name_input?.error = getString(R.string.empty_field)
            if (property.address.city.isEmpty())
                city_input?.error = getString(R.string.empty_field)
            if (property.address.postalCode.isEmpty())
                postal_code_input?.error = getString(R.string.empty_field)
            valid = false
            emptyFieldsInAddress = true
        }

        if (property.price <= 0 || property.surface <= 0 || property.numberOfRooms <= 0) {
            if (property.price <= 0)
                price_input?.error = getString(R.string.empty_field)
            if (property.surface <= 0)
                surface_input?.error = getString(R.string.empty_field)
            if (property.numberOfRooms <= 0)
                nbr_of_rooms_input?.error = getString(R.string.empty_field)
            valid = false
            emptyFieldsInMainInfo = true
        }
        return valid
    }

    private fun showAlertDialog() {
        var message = "Please fill in the fields "
        if (emptyFieldsInAddress) {
            message += "in Address page "
        }
        if (emptyFieldsInMainInfo) {
            if (emptyFieldsInAddress)
                message += "and "
            message += "in Main Informations page"
        }
        message += "."

        AlertDialog.Builder(this)
                .setTitle("Empty fields!")
                .setMessage(message)
                .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
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
        notificationHelper.manager?.notify(NotificationHelper.NOTIFICATION_ID, nb.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRestarted = false
    }

    companion object {
        const val TAG = "AddPropertyActivity"
    }
}