package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : BaseFragment(), OnMapReadyCallback {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val propertyDetailView = activity?.findViewById<View?>(R.id.frame_property_details)
        if (propertyDetailView?.visibility == VISIBLE) {
            btn_map_size.visibility = VISIBLE
            btn_map_size.setOnClickListener {
                if (propertyDetailView.visibility == VISIBLE) {
                    propertyDetailView.visibility = GONE
                    btn_map_size.text = "Réduire"
                } else {
                    propertyDetailView.visibility = VISIBLE
                    btn_map_size.text = "Agrandir"
                }
            }
        } else if (propertyDetailView == null)
            btn_map_size.visibility = GONE
    }

    override fun getLayout() = R.layout.fragment_map

    override fun onMapReady(googleMap: GoogleMap) {
        initMap()
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
}