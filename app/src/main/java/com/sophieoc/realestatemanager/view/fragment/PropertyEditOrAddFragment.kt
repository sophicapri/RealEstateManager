package com.sophieoc.realestatemanager.view.fragment

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
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
import kotlinx.android.synthetic.main.fragment_edit_add_property.*
import kotlinx.android.synthetic.main.pictures_property_edit_format.view.*
import java.util.*
import kotlin.collections.ArrayList


class PropertyEditOrAddFragment : BaseFragment() {
    var property = Property()
    private lateinit var adapter: PicturesAdapter

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
            title_edit_create.text = "Edit property"
        }
        if (arguments == null) title_edit_create.text = "Add a property"
        toolbar.setNavigationOnClickListener { mainContext.onBackPressed() }
        btn_save_property.setOnClickListener { saveChanges(this.property) }
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
        price_input.text.insert(0, property.price.toString())
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
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }

    private fun saveChanges(property: Property) {
        property.address.streetNumber = street_nbr_input.text.toString()
        property.address.apartmentNumber = apartment_nbr_input.text.toString()
        property.address.streetName = street_name_input.text.toString()
        property.address.city = city_input.text.toString()
        property.address.postalCode = postal_code_input.text.toString()
        property.address.region = region_input.text.toString()
        property.address.country = country_input.text.toString()
        property.type = PropertyType.values()[types_spinner.selectedItemPosition]
        property.price = price_input.text.toString().toInt()
        property.numberOfBedrooms = nbr_of_beds_input.text.toString().toInt()
        property.numberOfBathrooms = nbr_of_bath_input.text.toString().toInt()
        property.surface = surface_input.text.toString().toInt()
        property.availability = PropertyAvailability.values()[availability_spinner.selectedItemPosition]
        property.description = description_input.text.toString()
        updatePictures(property)
        setDates(property)
        if (property.pointOfInterests.isEmpty()) setPointOfInterestsAndSave(property)
        else saveProperty(property)
    }

    private fun updatePictures(property: Property) {
        adapter.pictures.forEachIndexed { index, photo ->
            photo.description = recycler_view_pictures[index].picture_description_input.text.toString()
        }
        property.photos = adapter.pictures
    }

    private fun setDates(property: Property) {
        if (property.dateOnMarket == null) property.dateOnMarket = Date()
        if (property.availability == PropertyAvailability.SOLD && property.dateSold == null) property.dateSold = Date()
        if (property.dateSold != null && property.availability == PropertyAvailability.AVAILABLE) property.dateSold = null
    }

    private fun setPointOfInterestsAndSave(property: Property) {
        val strLocation = property.address.toLatLng(mainContext).toStringFormat()
        val location = Location(property.id)
        location.latitude = property.address.toLatLng(mainContext).latitude
        location.longitude = property.address.toLatLng(mainContext).longitude
        if (strLocation != LAT_LNG_NOT_FOUND) {
            viewModel.getPointOfInterests(strLocation).observe(this, { placeDetailList ->
                placeDetailList?.let {
                    if (placeDetailList.isNotEmpty()) {
                        val listPointOfInterest = ArrayList<PointOfInterest>()
                        placeDetailList.forEachIndexed { index, placeDetails ->
                            if (placeDetails.types?.get(0) != POINT_OF_INTEREST) {
                                val pointOfInterest = PointOfInterest()
                                pointOfInterest.type = placeDetails.types?.get(0).toString().capitalize(Locale.ROOT).replace('_', ' ')
                                pointOfInterest.name = placeDetails.name.toString()
                                pointOfInterest.address = placeDetails.vicinity.toString()
                                pointOfInterest.distance = placeDetails.getDistanceFrom(location)
                                listPointOfInterest.add(pointOfInterest)
                            }
                            if ((index + 1) == placeDetailList.size) {
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
            //TODO : send notification
            it?.let {
                mainContext.onBackPressed()
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_edit_add_property
    }

    companion object{
        const val TAG = "PropertyEditFragment"
    }
}