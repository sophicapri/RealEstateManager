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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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
import kotlinx.android.synthetic.main.activity_edit_add_property.*
import kotlinx.android.synthetic.main.fragment_add_address.*
import kotlinx.android.synthetic.main.fragment_add_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class EditOrAddPropertyActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener, DialogInterface.OnDismissListener {
    private var fragmentAddress: Fragment = AddAddressFragment()
    var fragmentPropertyInfo: Fragment = AddPropertyInfoFragment()
    var fragmentPictures: Fragment = AddPicturesFragment()
    var activityRestarted = false
    private var emptyFieldsInAddress = false
    private var emptyFieldsInMainInfo = false
    lateinit var binding: ActivityEditAddPropertyBinding
    val propertyViewModel by viewModel<PropertyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        bindViews()
        super.onCreate(savedInstanceState)
    }

    private fun bindViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_add_property)
        binding.propertyViewModel = propertyViewModel
        intent.extras?.let {
            title_edit_create.text = getString(R.string.edit_property_title)
        }
        if (intent.extras == null) title_edit_create.text = getString(R.string.add_property_title)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        bottom_navigation_bar.setOnNavigationItemSelectedListener(this)
        bottom_navigation_bar.setBackgroundColor(ContextCompat.getColor(this, R.color.translucent_scrim_top_center))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        activityRestarted = true
        supportFragmentManager.findFragmentByTag(AddAddressFragment()::class.java.simpleName)?.let { fragmentAddress = it }
        supportFragmentManager.findFragmentByTag(AddPropertyInfoFragment()::class.java.simpleName)?.let { fragmentPropertyInfo = it }
        supportFragmentManager.findFragmentByTag(AddPicturesFragment()::class.java.simpleName)?.let { fragmentPictures = it }
    }

    override fun onResume() {
        super.onResume()
        if (!activityRestarted && !fragmentAddress.isAdded) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.frame_add_property, fragmentAddress, fragmentAddress::class.java.simpleName)
                    .add(R.id.frame_add_property, fragmentPropertyInfo, fragmentPropertyInfo::class.java.simpleName)
                    .add(R.id.frame_add_property, fragmentPictures, fragmentPictures::class.java.simpleName)
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
                propertyViewModel.property.userId = PreferenceHelper.currentUserId
                updatePointOfInterestsAndSave(propertyViewModel.property)
            } else
                showAlertDialog()
        } else {
            Toast.makeText(this, getString(R.string.cant_save_property), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun checkDates() {
        if(propertyViewModel.property.availability == PropertyAvailability.AVAILABLE)
            propertyViewModel.property.dateSold = null
        else
            propertyViewModel.property.dateOnMarket = null
    }

    private fun updatePointOfInterestsAndSave(property: Property) {
        val strLocation = property.address.toLatLng(this).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(this).latitude
        location.longitude = property.address.toLatLng(this).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            propertyViewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
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

    private fun saveProperty() {
        propertyViewModel.upsertProperty()
        propertyViewModel.propertySaved.observe(this, {
            it?.let {
                onBackPressed()
                binding.progressBar.visibility = GONE
                displayNotification()
            }
        })
    }

    private fun checkInputs(): Boolean {
        return checkAddressPage(propertyViewModel.property).and(checkMainInfoPage(propertyViewModel.property))
    }

    private fun checkMainInfoPage(property: Property): Boolean {
        emptyFieldsInMainInfo = false
        try {
            if (property.price <= 0 || property.surface <= 0 || property.numberOfRooms <= 0 ||
                    (property.dateSold == null && property.dateOnMarket == null)) {
                if (property.price <= 0)
                    price_input?.error = getString(R.string.empty_field)
                if (property.surface <= 0)
                    surface_input?.error = getString(R.string.empty_field)
                if (property.numberOfRooms <= 0)
                    nbr_of_rooms_input?.error = getString(R.string.empty_field)
                if (property.dateSold == null && property.dateOnMarket == null) {
                    error_date?.visibility = VISIBLE
                    error_date?.text = getString(R.string.please_select_a_date)
                }
                emptyFieldsInMainInfo = true
                return false
            }
        } catch (e: NullPointerException) {
            Log.d(TAG, "checkMainInfoPage: ${e.stackTrace}")
        }
        return true
    }

    private fun checkAddressPage(property: Property): Boolean {
        emptyFieldsInAddress = false
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
            emptyFieldsInAddress = true
            return false
        }
        return true
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
        notificationHelper.manager?.notify(NotificationHelper.NOTIFICATION_ID, nb.build())
    }

    private fun showAddressErrorDialog() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_cant_locate_address))
                .setMessage(getString(R.string.address_not_found))
                .setPositiveButton(getString(R.string.edit_address_btn)) { _, _ -> showFragment(fragmentAddress) }
                .setNegativeButton(getString(R.string.ignore_btn)) { dialog, _ -> dialog.dismiss() }
                .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRestarted = false
    }

    override fun getLayout() = Pair(null, binding.root)

    companion object {
        const val TAG = "AddPropertyActivity"
    }
}