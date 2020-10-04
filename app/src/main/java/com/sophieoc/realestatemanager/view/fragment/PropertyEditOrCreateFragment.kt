package com.sophieoc.realestatemanager.view.fragment

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.model.PointOfInterest
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.*
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_edit_create_property.*

class PropertyEditOrCreateFragment : BaseFragment() {
    var property = Property()
    lateinit var adapter: PicturesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        var savedState: Bundle? = null
        when {
            mainContext.intent.hasExtra(ADD_PROPERTY_KEY) -> {
                savedState = mainContext.intent.extras?.get(ADD_PROPERTY_KEY) as Bundle
            }
        }
        return super.onCreateView(inflater, container, savedState)
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            getPropertyId(it)
        }
    }

    private fun getPropertyId(arguments: Bundle?) {
        try {
            if (requireArguments().containsKey(PROPERTY_KEY)) {
                val propertyId = arguments?.get(PROPERTY_KEY) as String
                if (propertyId.isNotEmpty())
                    getProperty(propertyId)
            }
        } catch (e: IllegalStateException) {
            Log.e("TAG", "getPropertyIdFromArgs: " + e.message)
        }
    }

    private fun getProperty(propertyId: String) {
        viewModel.getPropertyById(propertyId).observe(mainContext, Observer {
            it?.let {
                property = it
                bindViews(it)
            }
        })
    }

    private fun bindViews(property: Property) {
        street_nbr_input.text.insert(0, property.address.streetNumber)
        apartment_nbr_input.text.insert(0, property.address.apartmentNumber)
        street_name_input.text.insert(0, property.address.streetName)
        city_input.text.insert(0, property.address.city)
        postal_code_input.text.insert(0, property.address.postalCode)
        region_input.text.insert(0, property.address.region)
        country_input.text.insert(0, property.address.country)
        types_spinner.setSelection(getSpinnerPosition(property.type.s, R.array.property_types))
        price_input.text.insert(0, property.price)
        nbr_of_beds_input.text.insert(0, property.numberOfBedrooms.toString())
        nbr_of_bath_input.text.insert(0, property.numberOfBathrooms.toString())
        surface_input.text.insert(0, property.surface.toString())
        availability_spinner.setSelection(getSpinnerPosition(property.availability.s, R.array.property_availability))
        description_input.text.insert(0, property.description)
        configureRecyclerView(property.photos)
    }

    private fun configureRecyclerView(photos: List<Photo>) {
        recycler_view_pictures.setHasFixedSize(true)
        recycler_view_pictures.layoutManager = LinearLayoutManager(context)
        adapter = PicturesAdapter(Glide.with(this))
        adapter.updatePictures(ArrayList(photos))
        recycler_view_pictures.adapter = adapter
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s ->
            if (s == value)
                position = index
        }
        return position
    }

    fun saveChanges(property: Property) {
        property.photos = adapter.pictures
        setPointOfInterests(property)
    }

    private fun setPointOfInterests(property: Property) {
        println("address found = " + property.address.toString())
        val strLocation = property.address.toLatLng(mainContext).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(mainContext).latitude
        location.longitude = property.address.toLatLng(mainContext).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            viewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
                if (placeDetailList != null && placeDetailList.isNotEmpty()) {
                    val listPointOfInterest = ArrayList<PointOfInterest>()
                    for (placeDetails in placeDetailList) {
                        if (placeDetails.types?.get(0) != POINT_OF_INTEREST) {
                            val pointOfInterest = PointOfInterest()
                            pointOfInterest.type = placeDetails.types?.get(0).toString()
                            pointOfInterest.name = placeDetails.name.toString()
                            pointOfInterest.address = placeDetails.vicinity.toString()
                            pointOfInterest.distance = placeDetails.getDistanceFrom(location)
                            listPointOfInterest.add(pointOfInterest)
                            if (listPointOfInterest.size == placeDetailList.size) {
                                property.pointOfInterests = listPointOfInterest
                                saveProperty(property)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun saveProperty(property: Property) {
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
        return R.layout.fragment_edit_create_property
    }
}