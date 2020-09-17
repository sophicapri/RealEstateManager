package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.LAT_LNG_NOT_FOUND
import com.sophieoc.realestatemanager.utils.toStringFormat
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.UserProfileActivity
import kotlinx.android.synthetic.main.fragment_property_detail.*

class PropertyDetailFragment : BaseFragment() {
    val property: Property = Property()
    override fun getLayout() = R.layout.fragment_property_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        open_user_profile.setOnClickListener {
            val intent = Intent(activity, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    fun editMode() {
        getPointOfInterests(property)
    }

    private fun getPointOfInterests(property: Property) {
        println("property received = " + property.address.toString())
        val strLocation = property.address.toLatLng(mainContext).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(mainContext).latitude
        location.longitude = property.address.toLatLng(mainContext).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            viewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
                if (placeDetailList != null && placeDetailList.isNotEmpty()) {
                    val listPointOfInterest = ArrayList<PointOfInterest>()
                    for (placeDetails in placeDetailList) {
                        if (placeDetails.types?.get(0) != "point_of_interest") {
                            val pointOfInterest = PointOfInterest()
                            pointOfInterest.type = placeDetails.types?.get(0).toString()
                            pointOfInterest.name = placeDetails.name.toString()
                            pointOfInterest.address = placeDetails.vicinity.toString()
                            pointOfInterest.distance = placeDetails.getDistanceFrom(location)
                            pointOfInterest.propertyId = property.id
                            if (listPointOfInterest.size < 3) {
                                println("list point of interest")
                                listPointOfInterest.add(pointOfInterest)
                                if (listPointOfInterest.size == 2) {
                                    println("point of interest == 2")
                                    property.pointOfInterests = listPointOfInterest
                                    savePointOfInterests(property)
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun savePointOfInterests(property: Property) {
        viewModel.upsertProperty(property).observe(this, Observer {
            println("hello null ")
            if (it != null) {
                println("point of interest 1 = ${it.pointOfInterests[0].name} à ${it.pointOfInterests[0].distance}m")
                println("point of interest 2 = ${it.pointOfInterests[1].name} à ${it.pointOfInterests[1].distance}m")
                println("point of interest 3 = ${it.pointOfInterests[2].name} à ${it.pointOfInterests[2].distance}m")
            }
        })
    }
}