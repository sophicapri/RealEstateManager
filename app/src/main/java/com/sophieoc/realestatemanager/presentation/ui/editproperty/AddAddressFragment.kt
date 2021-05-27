package com.sophieoc.realestatemanager.presentation.ui.editproperty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddAddressBinding
import com.sophieoc.realestatemanager.presentation.ui.PropertyViewModel
import com.sophieoc.realestatemanager.presentation.ui.editproperty.EditAddPropertyFragment.Companion.emptyFieldsInAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : Fragment() {
    private var _binding: FragmentAddAddressBinding? = null
    private val binding: FragmentAddAddressBinding
        get() = _binding!!
    private val sharedViewModel by activityViewModels<PropertyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView: ${binding.propertyViewModel?.property?.description}")
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            propertyViewModel = sharedViewModel
            executePendingBindings()
        }
        return binding.root
    }

    fun checkAddressPage(): Boolean {
        emptyFieldsInAddress = false
        binding.apply {
            sharedViewModel.apply {
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
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AddAddressFragment"
    }
}