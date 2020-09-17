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
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.LAT_LNG_NOT_FOUND
import com.sophieoc.realestatemanager.utils.toStringFormat
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.UserProfileActivity
import kotlinx.android.synthetic.main.fragment_property_detail.*

class PropertyDetailFragment : BaseFragment() {
    override fun getLayout() = R.layout.fragment_property_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        open_user_profile.setOnClickListener {
            val intent = Intent(activity, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    fun editMode(){
        val property = Property()
        property.address.streetNumber = "21"
        property.address.city = "Arpajon"
        property.address.streetName = "Edouard Robert"
        property.address.postalCode = "91290"
        property.address.country = "France"

        // How to get PointOfInterests
        val strLocation = property.address.toLatLng(mainContext).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(mainContext).latitude
        location.longitude = property.address.toLatLng(mainContext).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            viewModel.getPointOfInterests(strLocation).observe(this, Observer { pointOfInterestList ->
                if (pointOfInterestList != null && pointOfInterestList.isNotEmpty())
                    for (pointOfInterest in pointOfInterestList)
                        if (pointOfInterest.types?.get(0) != "point_of_interest") {
                            println("type = ${pointOfInterest.types?.toString()} \n name = ${pointOfInterest.name} \n " +
                                    "address = ${pointOfInterest.vicinity} \n " +
                                    "distance = ${pointOfInterest.getDistanceFrom(location)}m")
                        }
            })
        }
    }
}