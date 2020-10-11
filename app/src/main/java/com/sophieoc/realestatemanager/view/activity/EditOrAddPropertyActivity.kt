package com.sophieoc.realestatemanager.view.activity

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.model.json_to_java.PlaceDetails
import com.sophieoc.realestatemanager.notification.NotificationHelper
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddAddressFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPropertyInfoFragment
import kotlinx.android.synthetic.main.activity_edit_add_property.*
import java.util.*
import kotlin.collections.ArrayList

class EditOrAddPropertyActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var fragmentAddress: Fragment = AddAddressFragment()
    private var fragmentPropertyInfo: Fragment = AddPropertyInfoFragment()
    private var fragmentPictures: Fragment = AddPicturesFragment()

    override fun getLayout() = R.layout.activity_edit_add_property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            title_edit_create.text = "Edit property"
        }
        if (intent.extras == null) title_edit_create.text = "Add a property"
        toolbar.setNavigationOnClickListener { onBackPressed() }
        btn_save_property.setOnClickListener { saveChanges(BaseEditPropertyFragment.updatedProperty) }
        bottom_navigation_bar.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
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
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_add_property, fragment, fragment::class.java.simpleName).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.address_page -> showFragment(fragmentAddress)
            R.id.property_info_page -> showFragment(fragmentPropertyInfo)
            R.id.pictures_page -> showFragment(fragmentPictures)
        }
        return true
    }

    private fun saveChanges(property: Property) {
        setDates(property)
        if (property.pointOfInterests.isEmpty()) setPointOfInterestsAndSave(property)
        else saveProperty(property)
    }

    private fun setDates(property: Property) {
        if (property.dateOnMarket == null) property.dateOnMarket = Date()
        if (property.availability == PropertyAvailability.SOLD && property.dateSold == null) property.dateSold = Date()
        if (property.dateSold != null && property.availability == PropertyAvailability.AVAILABLE) property.dateSold = null
    }

    private fun setPointOfInterestsAndSave(property: Property) {
        Log.d(TAG, "setPointOfInterestsAndSave: here")
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

    private fun setPointOfInterest(placeDetails: PlaceDetails, location: Location) : PointOfInterest {
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

    fun checkInputs() {

    }

    private fun saveProperty(property: Property) {
        Log.d(TAG, "saveProperty: property mainType = ${property.pointOfInterests[0].mainType}")
        //checkInputs()
        viewModel.upsertProperty(property).observe(this, Observer {
            it?.let {
                //TODO : Add progress bar
                displayNotification()
                onBackPressed()
            }
        })
    }

    private fun displayNotification() {
        val notificationHelper = NotificationHelper(this)
        val nb: NotificationCompat.Builder = notificationHelper
                .getChannelNotification(getString(R.string.property_saved_successful))
        notificationHelper.manager?.notify(NotificationHelper.NOTIFICATION_ID, nb.build())
    }

    /*  override fun onBackPressed() {
          if (fragmentPictures.isVisible || fragmentPropertyInfo.isVisible)
              bottom_navigation_bar.selectedItemId = R.id.address_page
          if (fragmentAddress.isVisible)
              super.onBackPressed()
      }

     */

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        activityRestarted = false
    }

    companion object {
        const val TAG = "AddPropertyActivity"
        var activityRestarted = false
    }
}