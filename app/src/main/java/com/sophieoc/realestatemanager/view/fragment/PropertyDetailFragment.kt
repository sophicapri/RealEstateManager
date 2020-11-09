package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.FragmentPropertyDetailBinding
import com.sophieoc.realestatemanager.databinding.NoPropertyClickedBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.MapActivity
import com.sophieoc.realestatemanager.view.activity.UserPropertiesActivity
import com.sophieoc.realestatemanager.view.adapter.PointOfInterestAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.toolbar_property_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PropertyDetailFragment : Fragment(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private lateinit var mainContext: BaseActivity
    private var propertyMarker: MarkerOptions? = null
    private var latLngProperty = LatLng(0.0, 0.0)
    private val propertyViewModel by viewModel<PropertyViewModel>()
    private val userViewModel by viewModel<UserViewModel>()
    lateinit var binding: FragmentPropertyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = activity as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_detail, container, false)
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.progressBar.visibility = VISIBLE
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainContext.checkConnection()
        mainContext.checkLocationEnabled()
        when {
            arguments != null -> getPropertyIdFromArgs(arguments)
            mainContext.intent.hasExtra(PROPERTY_ID) -> getPropertyIdFromIntent(mainContext.intent.extras)
            else -> displayNoPropertyFragment()
        }
        initMap()
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
                .replace(R.id.frame_property_details, NoPropertyClickedFragment(), NoPropertyClickedFragment()::class.java.simpleName).commit()
        binding.progressBar.visibility = View.GONE
    }

    private fun getProperty(propertyId: String) {
        propertyViewModel.getPropertyById(propertyId).observe(mainContext, {
            it?.let {
                binding.property = it
                map?.let { addMarkerAndZoom() }
                bindViews()
            }
        })
    }

    private fun initMap() {
        if (PreferenceHelper.locationEnabled) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }
    }

    private fun bindViews() {
        binding.viewPagerForSpringDots = binding.viewPager
        binding.propertyDetailToolbar.property_detail_toolbar.setNavigationOnClickListener { mainContext.onBackPressed() }
        binding.fabEditProperty.setOnClickListener { startEditPropertyActivity() }
        binding.btnMapFullscreen.setOnClickListener { startMapActivity() }
        binding.property?.let {
            binding.viewPager.registerOnPageChangeCallback(getOnPageChangeCallback(it.photos))
            configureRecyclerView(it.pointOfInterests)
            userViewModel.getUserById(it.userId).observe(mainContext, { agent ->
                agent?.let {
                    binding.user = agent.user
                    binding.icProfilePicture.setOnClickListener { startUserActivity() }
                    binding.username.setOnClickListener { startUserActivity() }
                    binding.titleAgent.setOnClickListener { startUserActivity() }
                }
                binding.progressBar.visibility = View.GONE
            })
        }
    }

    private fun getOnPageChangeCallback(photos: List<Photo>): ViewPager2.OnPageChangeCallback {
        return object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (photos.isNotEmpty() && position < photos.size)
                    binding.picDescription.text = photos[position].description
            }
        }
    }

    fun startMapActivity() {
        val intent = Intent(mainContext, MapActivity::class.java)
        intent.putExtra(LATITUDE_PROPERTY, latLngProperty.latitude)
        intent.putExtra(LONGITUDE_PROPERTY, latLngProperty.longitude)
        intent.putExtra(PROPERTY_ID, binding.property?.id)
        startActivity(intent)
    }

    private fun startEditPropertyActivity() {
        if (Utils.isInternetAvailable(mainContext)) {
            val intent = Intent(mainContext, EditOrAddPropertyActivity::class.java)
            intent.putExtra(PROPERTY_ID, binding.property?.id)
            startActivity(intent)
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(mainContext, getString(R.string.edit_add_unavailable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun startUserActivity() {
        val intent = Intent(mainContext, UserPropertiesActivity::class.java)
        intent.putExtra(USER_ID, binding.property?.userId)
        startActivity(intent)
    }

    private fun configureRecyclerView(pointOfInterests: List<PointOfInterest>) {
        val recyclerView = binding.recyclerViewPointOfInterest
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PointOfInterestAdapter(pointOfInterests)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (propertyMarker == null)
            addMarkerAndZoom()
    }

    private fun addMarkerAndZoom() {
        binding.property?.let { latLngProperty = it.address.toLatLng(mainContext) }
        if (latLngProperty.toString() != LAT_LNG_NOT_FOUND) {
            map?.clear()
            propertyMarker = MarkerOptions().position(latLngProperty)
                    .icon(R.drawable.ic_baseline_house_24.toBitmap(resources))
            map?.addMarker(propertyMarker)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngProperty, 17f))
        } else
            view?.let { Snackbar.make(it, getString(R.string.cant_locate_property), LENGTH_LONG).show() }
    }

    class NoPropertyClickedFragment : Fragment() {
        private lateinit var binding: NoPropertyClickedBinding

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = DataBindingUtil.inflate(inflater, R.layout.no_property_clicked, container, false)
            return binding.root
        }
    }

    companion object {
        const val TAG = "PropertyDetailFragment"
    }
}
