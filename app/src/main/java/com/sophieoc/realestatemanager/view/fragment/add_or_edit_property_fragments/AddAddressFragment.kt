package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.utils.PROPERTY_NOT_DEFINED
import kotlinx.android.synthetic.main.fragment_add_address.*


class AddAddressFragment : BaseEditPropertyFragment() {

    override fun onResume() {
        super.onResume()
        addPropertyActivity.intent.extras?.let{
            getPropertyId(it)
        }
        setInputListeners()
        Log.d(TAG, "onResume: ")
    }

    private fun setInputListeners() {
        //
    }

    private fun getPropertyId(extras: Bundle) {
        if (extras.containsKey(PROPERTY_ID)) {
            val propertyId = extras.get(PROPERTY_ID) as String
            getProperty(propertyId)
        }
    }

    private fun getProperty(propertyId: String) {
        viewModel.getPropertyById(propertyId).observe(addPropertyActivity, Observer {
            it?.let {
                if (updatedProperty.userId == PROPERTY_NOT_DEFINED || (city_input.text.isEmpty() && it.address.city.isNotEmpty())) {
                    updatedProperty = it
                    bindInputs()
                }
            }
        })
    }

    private fun bindInputs() {
        street_nbr_input.text.insert(0, updatedProperty.address.streetNumber)
        apartment_nbr_input.text.insert(0, updatedProperty.address.apartmentNumber)
        street_name_input.text.insert(0, updatedProperty.address.streetName)
        city_input.text.insert(0, updatedProperty.address.city)
        postal_code_input.text.insert(0, updatedProperty.address.postalCode)
        region_input.text.insert(0, updatedProperty.address.region)
        country_input.text.insert(0, updatedProperty.address.country)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_add_address
    }

    companion object{
        const val TAG = "AddAddressFragment"
    }
}