package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddAddressBinding
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity.Companion.emptyFieldsInAddress
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel


class AddAddressFragment : Fragment(){
    private lateinit var binding: FragmentAddAddressBinding
    private val sharedViewModel: PropertyViewModel by lazy { requireActivity().getViewModel() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = sharedViewModel
        if (EditOrAddPropertyActivity.activityRestarted)
            binding.executePendingBindings()
        return binding.root
    }

    fun checkAddressPage(property: Property): Boolean {
        emptyFieldsInAddress = false
        val streetNbrInput = binding.streetNbrInput
        val streetNameInput = binding.streetNameInput
        val cityInput = binding.cityInput
        val postalCodeInput = binding.postalCodeInput

        if (property.address.streetNumber.isEmpty() || property.address.streetName.isEmpty() ||
            property.address.city.isEmpty() || property.address.postalCode.isEmpty()
        ) {
            if (property.address.streetNumber.isEmpty())
                streetNbrInput.error = getString(R.string.empty_field)
            if (property.address.streetName.isEmpty())
                streetNameInput.error = getString(R.string.empty_field)
            if (property.address.city.isEmpty())
                cityInput.error = getString(R.string.empty_field)
            if (property.address.postalCode.isEmpty())
                postalCodeInput.error = getString(R.string.empty_field)
            emptyFieldsInAddress = true
            return false
        }
        return true
    }

    companion object {
        private const val TAG = "AddAddressFragment"
    }
}