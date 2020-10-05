package com.sophieoc.realestatemanager.view.activity

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddAddressFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPicturesFragment
import com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments.AddPropertyInfoFragment
import kotlinx.android.synthetic.main.activity_edit_add_property.*
import java.util.*
import kotlin.collections.ArrayList

class EditOrAddPropertyActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val fragmentAddress = AddAddressFragment()
    val fragmentPropertyInfo = AddPropertyInfoFragment()
    val fragmentPictures = AddPicturesFragment()

    override fun getLayout() = R.layout.activity_edit_add_property

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment(fragmentAddress)
        intent.extras?.let {
            title_edit_create.text = "Edit property"
        }
        if (intent.extras == null) title_edit_create.text = "Add a property"
        toolbar.setNavigationOnClickListener { onBackPressed() }
        btn_save_property.setOnClickListener { saveChanges(BaseEditPropertyFragment.updatedProperty) }
        bottom_navigation_bar.setOnNavigationItemSelectedListener(this)
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame_add_property, fragment).addToBackStack(null).commit()
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
       // setDates(property)
        if (property.pointOfInterests.isEmpty()) setPointOfInterestsAndSave(property)
        else saveProperty(property)
    }

    private fun setDates(property: Property) {
        if (property.dateOnMarket == null) property.dateOnMarket = Date()
        if (property.availability == PropertyAvailability.SOLD && property.dateSold == null) property.dateSold = Date()
        if (property.dateSold != null && property.availability == PropertyAvailability.AVAILABLE) property.dateSold = null
    }

    private fun setPointOfInterestsAndSave(property: Property) {
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
                            if (placeDetails.types?.get(0) != POINT_OF_INTEREST) {
                                val pointOfInterest = PointOfInterest()
                                pointOfInterest.type = placeDetails.types?.get(0).toString().capitalize(Locale.ROOT).replace('_', ' ')
                                pointOfInterest.name = placeDetails.name.toString()
                                pointOfInterest.address = placeDetails.vicinity.toString()
                                pointOfInterest.distance = placeDetails.getDistanceFrom(location)
                                listPointOfInterest.add(pointOfInterest)
                            }
                            if ((index + 1) == placeDetailList.size) {
                                property.pointOfInterests = listPointOfInterest
                                saveProperty(property)
                            }
                        }
                    }
                }
            })
        }
    }

    fun checkInputs(){

    }

    private fun saveProperty(property: Property) {
        Log.d(TAG, "saveProperty: property value = $property")
        //checkInputs()
     /*   viewModel.upsertProperty(property).observe(this, Observer {
            //TODO : send notification
            it?.let {
                onBackPressed()
            }
        })

      */
    }

    override fun onDestroy() {
        super.onDestroy()
       // property = Property()
    }

    companion object{
        const val TAG = "AddPropertyActivity"
    }
}