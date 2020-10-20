package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.UserPropertiesActivity
import com.sophieoc.realestatemanager.view.adapter.PointOfInterestAdapter
import com.sophieoc.realestatemanager.view.adapter.SliderAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_property_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PropertyDetailFragment : BaseFragment(), OnMapReadyCallback {
    var property: Property = Property()
    private var map: GoogleMap? = null
    private var propertyMarker: MarkerOptions? = null
    private var latLngProperty = LatLng(0.0,0.0)
    val propertyViewModel by viewModel<PropertyViewModel>()
    val userViewModel by viewModel<UserViewModel>()

    override fun getLayout() = Pair(R.layout.fragment_property_detail, null)

    override fun onResume() {
        super.onResume()
        when {
            arguments != null -> getPropertyIdFromArgs(arguments)
            mainContext.intent.hasExtra(PROPERTY_ID) -> getPropertyIdFromIntent(mainContext.intent.extras)
            else -> displayNoPropertyFragment()
        }
    }

    private fun getPropertyIdFromArgs(arguments: Bundle?) {
        try {
            if (requireArguments().containsKey(PROPERTY_ID)) {
                val propertyId = arguments?.get(PROPERTY_ID) as String
                if (propertyId.isNotEmpty())
                    getProperty(propertyId)
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "getPropertyIdFromArgs: " + e.message)
        }
    }

    private fun getPropertyIdFromIntent(extras: Bundle?) {
        val propertyId = extras?.get(PROPERTY_ID) as String
        getProperty(propertyId)
    }

    private fun displayNoPropertyFragment() {
        mainContext.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_property_details, NoPropertyClickedFragment()).commit()
    }

    private fun getProperty(propertyId: String) {
        propertyViewModel.getPropertyById(propertyId).observe(mainContext, {
            it?.let {
                property = it
                map?.let { addMarkerAndZoom() }
                bindViews(it)
            }
        })
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun bindViews(property: Property) {
        val listPhotos = if (property.photos.isNotEmpty()) property.photos
        else
            arrayListOf(Photo(NO_IMAGE_AVAILABLE_PROPERTY, ""))
        view_pager.adapter = SliderAdapter(listPhotos, Glide.with(this))
        pageChangeListener()
        if (property.photos.isNotEmpty()) spring_dots_indicator.setViewPager2(view_pager)
        if (property.photos.size == 1) spring_dots_indicator.visibility = GONE
        property_availability.text = property.availability.s.toUpperCase(Locale.ROOT)
        price_property.text = property.price.formatToDollars()
        type_property.text = property.type.s
        address_property.text = property.address.toString()
        nbr_of_beds_input.text = property.numberOfBedrooms.toString()
        nbr_of_bath.text = property.numberOfBathrooms.toString()
        nbr_of_rooms.text = property.numberOfRooms.toString()
        surface.text = property.surface.toString()
        showAgentInCharge(property)
        description.text = property.description
        configureRecyclerView(property.pointOfInterests)
        displayDate()
        fab_edit_property.setOnClickListener { startEditPropertyActivity(property.id) }
        property_detail_toolbar?.setNavigationOnClickListener {
            mainContext.onBackPressed()
        }
        btn_map_fullscreen.setOnClickListener { startMapActivity() }
    }

    private fun startMapActivity() {
        val intent = Intent(mainContext, MapActivity::class.java)
        intent.putExtra(LATITUDE_PROPERTY, latLngProperty.latitude)
        intent.putExtra(LONGITUDE_PROPERTY, latLngProperty.longitude)
        intent.putExtra(PROPERTY_ID, property.id)
        startActivity(intent)
    }

    private fun startEditPropertyActivity(propertyId: String) {
        if (Utils.isConnectionAvailable(mainContext)) {
            val intent = Intent(mainContext, EditOrAddPropertyActivity::class.java)
            intent.putExtra(PROPERTY_ID, propertyId)
            startActivity(intent)
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(mainContext, getString(R.string.edit_add_unavailable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun pageChangeListener() {
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (property.photos.isNotEmpty())
                    pic_description.text = property.photos[position].description
            }
        })
    }

    private fun displayDate() {
        if (property.dateOnMarket != null) {
            date_on_market.text = getString(R.string.on_the_market_since, property.dateOnMarket?.toStringFormat())
            date_sold.visibility = GONE
        }else {
            date_sold.text = getString(R.string.sold_since, property.dateSold?.toStringFormat())
            date_on_market.visibility = GONE
        }
    }

    private fun showAgentInCharge(property: Property) {
        userViewModel.getUserById(property.userId).observe(this, { agent ->
            agent?.let {
                Glide.with(this)
                        .load(it.user.urlPhoto)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ic_profile_picture)
                username.text = getString(R.string.underline, it.user.username)
                ic_profile_picture.setOnClickListener { _ -> startUserActivity(it.user.uid) }
                username.setOnClickListener { _ -> startUserActivity(it.user.uid) }
                title_agent.setOnClickListener { _ -> startUserActivity(it.user.uid) }
                userViewModel.currentUser.observe(mainContext, { loggedUser ->
                    loggedUser?.let{
                        if (loggedUser.user.uid != agent.user.uid) fab_edit_property.visibility = GONE
                        else fab_edit_property.visibility = VISIBLE
                    }
                })
            }
        })
    }

    private fun startUserActivity(uid: String) {
        val intent = Intent(mainContext, UserPropertiesActivity::class.java)
        intent.putExtra(USER_ID, uid)
        startActivity(intent)
    }

    private fun configureRecyclerView(pointOfInterests: List<PointOfInterest>) {
        recycler_view_point_of_interest.setHasFixedSize(true)
        recycler_view_point_of_interest.layoutManager = LinearLayoutManager(context)
        recycler_view_point_of_interest.adapter = PointOfInterestAdapter(pointOfInterests)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (property.address.toString().isNotEmpty() && propertyMarker == null)
            addMarkerAndZoom()
    }

    private fun addMarkerAndZoom() {
        latLngProperty = property.address.toLatLng(mainContext)
        if (latLngProperty.toString() != LAT_LNG_NOT_FOUND) {
            map?.clear()
            propertyMarker = MarkerOptions().position(latLngProperty)
                    .icon(R.drawable.ic_baseline_house_24.toBitmap(resources))
            map?.addMarker(propertyMarker)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngProperty, 17f))
        } else
            view?.let { Snackbar.make(it, getString(R.string.cant_locate_property), LENGTH_LONG).show() }
    }

    class NoPropertyClickedFragment : BaseFragment() {
        override fun getLayout() = Pair(R.layout.no_property_clicked, null)
    }

    companion object {
        const val TAG = "PropertyDetailFragment"
    }
}
