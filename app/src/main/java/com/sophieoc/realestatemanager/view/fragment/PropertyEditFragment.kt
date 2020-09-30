package com.sophieoc.realestatemanager.view.fragment

import android.location.Location
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.LAT_LNG_NOT_FOUND
import com.sophieoc.realestatemanager.utils.toStringFormat

class PropertyEditFragment: BaseFragment() {

    fun editMode(property: Property) {
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
                            if (listPointOfInterest.size < 3) {
                                listPointOfInterest.add(pointOfInterest)
                                if (listPointOfInterest.size == 2) {
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

    override fun getLayout(): Int {
        return R.layout.fragment_property_edit
    }
}