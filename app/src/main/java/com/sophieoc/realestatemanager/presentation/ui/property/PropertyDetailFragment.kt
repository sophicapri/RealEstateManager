package com.sophieoc.realestatemanager.presentation.ui.property

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentPropertyDetailBinding
import com.sophieoc.realestatemanager.databinding.NoPropertyClickedBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.presentation.BaseActivity
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel
import com.sophieoc.realestatemanager.presentation.ui.UserViewModel
import com.sophieoc.realestatemanager.presentation.ui.editproperty.EditAddPropertyActivity
import com.sophieoc.realestatemanager.presentation.ui.map.MapActivity
import com.sophieoc.realestatemanager.presentation.ui.userproperty.UserPropertiesActivity
import com.sophieoc.realestatemanager.presentation.ui.userproperty.UserUiState
import com.sophieoc.realestatemanager.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PropertyDetailFragment : Fragment(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private lateinit var mainContext: BaseActivity
    private var propertyMarker: MarkerOptions? = null
    private var latLngProperty = LatLng(0.0, 0.0)
    private val propertyViewModel by viewModels<PropertyViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private var _binding: FragmentPropertyDetailBinding? = null
    private val binding: FragmentPropertyDetailBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = activity as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_property_detail, container, false
        )
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
            .replace(
                R.id.frame_property_details,
                NoPropertyClickedFragment(),
                NoPropertyClickedFragment()::class.java.simpleName
            ).commit()
        binding.progressBar.visibility = View.GONE
    }

    private fun getProperty(propertyId: String) {
        lifecycleScope.launchWhenStarted {
            propertyViewModel.getPropertyById(propertyId).collect { propertyUiState ->
                when (propertyUiState) {
                    is PropertyUiState.Success -> {
                        binding.property = propertyUiState.property
                        map?.let { addMarkerAndZoom() }
                        bindViews()
                    }
                    is PropertyUiState.Error -> {
                       /* TODO() */
                    }
                    is PropertyUiState.Loading -> {
                     /*   TODO()*/
                    }
                }
            }
        }
    }

    private fun initMap() {
        if (PreferenceHelper.locationEnabled) {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }
    }

    private fun bindViews() {
        binding.apply {
            fragment = this@PropertyDetailFragment
            loggedUserId = PreferenceHelper.currentUserId
            lifecycleOwner = viewLifecycleOwner
            propertyDetailToolbar.propertyDetailToolbar.setNavigationOnClickListener { mainContext.onBackPressed() }
            fabEditProperty.setOnClickListener { startEditPropertyActivity() }
            btnMapFullscreen.setOnClickListener { startMapActivity() }
        }
        binding.property?.let { property ->
            binding.viewPager.registerOnPageChangeCallback(getOnPageChangeCallback(property.photos))
            lifecycleScope.launchWhenStarted {
                userViewModel.getUserById(property.userId).collect { userUiState ->
                    when (userUiState) {
                        is UserUiState.Success -> {
                            binding.apply {
                                user = userUiState.userWithProperties.user
                                icProfilePicture.setOnClickListener { startUserActivity() }
                                username.setOnClickListener { startUserActivity() }
                                titleAgent.setOnClickListener { startUserActivity() }
                                progressBar.visibility = View.GONE

                                property.photos.let { photos ->
                                    val listPhotos = if (photos.isNotEmpty()) photos
                                    else
                                        arrayListOf(Photo("", ""))
                                    viewPager.adapter = SliderAdapter(listPhotos)
                                    TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
                                }
                            }
                        }
                        is UserUiState.Error -> {/*TODO:*/ }
                        is UserUiState.Loading -> {/*TODO:*/ }
                    }
                }
            }
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
            val intent = Intent(mainContext, EditAddPropertyActivity::class.java)
            intent.putExtra(PROPERTY_ID, binding.property?.id)
            startActivity(intent)
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(mainContext, getString(R.string.edit_add_unavailable), Toast.LENGTH_LONG)
                .show()
            PreferenceHelper.internetAvailable = false
        }
    }

    private fun startUserActivity() {
        val intent = Intent(mainContext, UserPropertiesActivity::class.java)
        intent.putExtra(USER_ID, binding.property?.userId)
        startActivity(intent)
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
            map?.addMarker(propertyMarker!!)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngProperty, 17f))
        } else
            view?.let {
                Snackbar.make(it, getString(R.string.cant_locate_property), LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class NoPropertyClickedFragment : Fragment() {
        private var _binding: NoPropertyClickedBinding? = null
        private val binding: NoPropertyClickedBinding
            get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = DataBindingUtil.inflate(
                inflater, R.layout.no_property_clicked, container, false
            )
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    companion object {
        private const val TAG = "PropertyDetailFragment"
    }
}
