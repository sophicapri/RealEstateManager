package com.sophieoc.realestatemanager.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mainContext : BaseActivity
    private var map: GoogleMap? = null
    private var propertyDetailView: View? = null
    private lateinit var currentLocation: Location
    private var fragmentNotRestarted = true
    val propertyViewModel by viewModel<PropertyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainContext = activity as BaseActivity
        initMap()
        propertyDetailView = activity?.findViewById(R.id.frame_property_details)
        refocus_btn.setOnClickListener {
            if (PreferenceHelper.locationEnabled) {
                fetchLastLocation()
            } else
                mainContext.checkLocationEnabled()
        }
        progressBar.visibility = VISIBLE
        mainContext.my_toolbar.setNavigationOnClickListener { mainContext.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        mainContext.checkConnection()
        mainContext.checkLocationEnabled()
        handleMapSize()
        if (fragmentNotRestarted && getLocationFromIntent() == null)
            fetchLastLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.setOnInfoWindowClickListener { marker: Marker? ->
            if (marker != null) startPropertyDetail(marker)
        }
        map = googleMap
        if (mainContext.intent.hasExtra(LATITUDE_PROPERTY) && mainContext.intent.hasExtra(LONGITUDE_PROPERTY))
            getLocationFromIntent()?.let { it -> focusMap(it)
                progressBar.visibility = GONE
            }
        initMarkers()
        if (PreferenceHelper.locationEnabled)
            map?.isMyLocationEnabled = true
        else
            progressBar.visibility = GONE
    }

    private fun startPropertyDetail(marker: Marker) {
        if (propertyDetailView == null) {
            val intent = Intent(mainContext, PropertyDetailActivity::class.java)
            intent.putExtra(PROPERTY_ID, marker.tag.toString())
            mainContext.startActivityForResult(intent, RQ_CODE_PROPERTY)
        } else {
            propertyDetailView?.visibility = VISIBLE
            btn_map_size.text = getString(R.string.fullscreen)
            val bundle = Bundle()
            bundle.putString(PROPERTY_ID, marker.tag.toString())
            val propertyDetailFragment = PropertyDetailFragment()
            propertyDetailFragment.arguments = bundle
            mainContext.supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_property_details, propertyDetailFragment).commit()
        }
    }

    private fun handleMapSize() {
        if (propertyDetailView?.visibility == VISIBLE) {
            btn_map_size.visibility = VISIBLE
            btn_map_size.setOnClickListener {
                if (propertyDetailView?.visibility == VISIBLE) {
                    propertyDetailView?.visibility = GONE
                    btn_map_size.text = getString(R.string.reduce_map)
                } else {
                    propertyDetailView?.visibility = VISIBLE
                    btn_map_size.text = getString(R.string.fullscreen)
                }
            }
        } else if (propertyDetailView == null)
            btn_map_size.visibility = GONE
    }

    @SuppressLint("MissingPermission")
    fun fetchLastLocation() {
        if (PreferenceHelper.locationEnabled) {
            val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainContext as MapActivity)
            val task: Task<Location?> = fusedLocationProviderClient.lastLocation
            task.addOnCompleteListener { getLocationTask: Task<Location?> ->
                if (getLocationTask.isSuccessful) {
                    val currentLocation = getLocationTask.result
                    if (currentLocation != null && currentLocation.longitude != 0.0 && currentLocation.longitude != 0.0) {
                        this.currentLocation = currentLocation
                        focusMap(currentLocation)
                        if (progressBar != null ) progressBar.visibility = GONE
                    } else {
                        // -> update view after location enabled
                        mainContext.supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
                    }
                }else {
                    Toast.makeText(activity, R.string.cant_get_location, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun focusMap(currentLocation: Location) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5F))
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun initMarkers() {
        propertyViewModel.getProperties().observe(mainContext, { propertyList ->
            if (propertyList != null)
                for (property in propertyList) {
                    if (property.address.toString().isNotEmpty()) {
                        val latLng = property.address.toLatLng(mainContext)
                        if (latLng.toStringFormat() != LAT_LNG_NOT_FOUND) {
                            val marker: Marker? = map?.addMarker(MarkerOptions().title(property.type.toString())
                                    .position(latLng)
                                    .icon(R.drawable.ic_baseline_house_24.toBitmap(resources)))
                            marker?.tag = property.id
                        }

                    }
                }
        })
    }

    private fun getLocationFromIntent(): Location? {
        val extras = mainContext.intent.extras
        val lat = extras?.get(LATITUDE_PROPERTY) as Double?
        val lng = extras?.get(LONGITUDE_PROPERTY) as Double?
        val location = Location("")
        lat?.let { location.latitude = it }
        lng?.let { location.longitude = it }
        lat?.let { return location }
        return null
    }

    override fun onPause() {
        super.onPause()
        fragmentNotRestarted = false
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentNotRestarted = true
    }
}