package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import kotlinx.android.synthetic.main.fragment_add_address.*


class AddAddressFragment : BaseEditPropertyFragment() {
    override fun onResume() {
        super.onResume()
        addPropertyActivity.intent.extras?.let{
            getPropertyId(it)
        }
    }

    private fun getPropertyId(extras: Bundle) {
        if (extras.containsKey(PROPERTY_ID)) {
            val propertyId = extras.get(PROPERTY_ID) as String
            getProperty(propertyId)
        }
    }

    private fun getProperty(propertyId: String) {
        viewModel.getPropertyById(propertyId).observe(this, Observer {
            it?.let {
                updatedProperty = it
                bindViews()
            }
        })
    }

    private fun bindViews() {
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