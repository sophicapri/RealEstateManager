package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.LAT_LNG_NOT_FOUND
import com.sophieoc.realestatemanager.utils.PROPERTY_KEY
import kotlinx.android.synthetic.main.fragment_property_detail.*


class PropertyDetailFragment : BaseFragment(), OnMapReadyCallback {
    var property: Property = Property()
    var map: GoogleMap? = null
    var marker: MarkerOptions? = null

    override fun getLayout() = R.layout.fragment_property_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when {
            arguments != null -> {
                try {
                    if (requireArguments().containsKey(PROPERTY_KEY)) {
                        property_detail_view.visibility = VISIBLE
                        val propertyId = arguments?.get(PROPERTY_KEY) as String
                        if (propertyId.isNotEmpty())
                            getProperty(propertyId)
                    }
                } catch (e: IllegalStateException) {
                    Log.e("TAG", "getLayout: " + e.message)
                }
            }
            mainContext.intent.hasExtra(PROPERTY_KEY) -> {
                property_detail_view.visibility = VISIBLE
                val propertyId = mainContext.intent.extras?.get(PROPERTY_KEY) as String
                getProperty(propertyId)
            }
            else -> {
                no_property_clicked.visibility = VISIBLE
                property_detail_view.visibility = GONE
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (property.address.toString().isNotEmpty() && marker == null)
            addMarkerAndZoom()
    }

    private fun addMarkerAndZoom() {
        val latLng = property.address.toLatLng(mainContext)
        if (latLng.toString() != LAT_LNG_NOT_FOUND) {
            marker = MarkerOptions().position(latLng)
            map?.addMarker(marker)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
    }

    private fun getProperty(propertyId: String) {
        no_property_clicked.visibility = GONE
        viewModel.getPropertyById(propertyId).observe(mainContext, Observer {
            if (it != null) {
                property = it
                if (map != null) {
                    addMarkerAndZoom()
                }
            }
        })
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

}
