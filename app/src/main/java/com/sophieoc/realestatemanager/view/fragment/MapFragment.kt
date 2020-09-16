package com.sophieoc.realestatemanager.view.fragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.sophieoc.realestatemanager.AppController
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.utils.toBitmap
import com.sophieoc.realestatemanager.view.activity.MainActivity.Companion.TAG
import com.sophieoc.realestatemanager.view.activity.MapActivity
import kotlinx.android.synthetic.main.fragment_map.*

@Suppress("SameParameterValue")
class MapFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMap()
        handleMapSize()
        refocus_btn.setOnClickListener {
            AppController.instance.currentLocation?.let { location -> focusMap(location) }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: ")
        map = googleMap
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false
        map.uiSettings.isMapToolbarEnabled = false
        map.setOnInfoWindowClickListener(OnInfoWindowClickListener { marker: Marker? ->
          //   startPropertyDetail(marker)
            })
        fetchLastLocation()
        initMarkers()
    }

    private fun handleMapSize() {
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

    private fun fetchLastLocation() {
        val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainContext)
        if (context?.let { ActivityCompat.checkSelfPermission(it, ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }
        checkLocationEnabled()
        val task: Task<Location?> = fusedLocationProviderClient.lastLocation
        task.addOnCompleteListener { getLocationTask: Task<Location?> ->
            if (getLocationTask.isSuccessful) {
                val currentLocation = getLocationTask.result
                if (currentLocation != null) {
                    focusMap(currentLocation)
                    //Init the current location for the entire app
                    AppController.instance.currentLocation = currentLocation
                }
            } else {
                Toast.makeText(activity, R.string.cant_get_location, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationEnabled() {
        if (!mainContext.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder(mainContext)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings) { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Companion.REQUEST_CODE_LOCATION)
                    }
                    .setNegativeButton(R.string.Cancel, null)
                    .show()
        }
    }

    private fun focusMap(currentLocation: Location) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5F))
    }

    override fun getLayout() = R.layout.fragment_map

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                fetchLastLocation()
        }else
            Log.d(TAG, "onRequestPermissionsResult: refused")
    }

    private fun initMarkers() {
        viewModel.getProperties().observe(mainContext, Observer { propertyList ->
            if (propertyList != null)
                for (property in propertyList) {
                    val lat = property.address.toLatLng(mainContext)?.latitude
                    val lng = property.address.toLatLng(mainContext)?.longitude
                    if (lat != null && lng != null) {
                        val marker: Marker = map.addMarker(MarkerOptions().title(property.type.toString())
                                .position(LatLng(lat, lng))
                                .icon(R.drawable.ic_baseline_house_24.toBitmap(resources)))
                        marker.tag = property.id
                    }
                }
        })
    }

    companion object {
        const val REQUEST_CODE_LOCATION = 321
        const val REQUEST_CODE = 123
    }
}