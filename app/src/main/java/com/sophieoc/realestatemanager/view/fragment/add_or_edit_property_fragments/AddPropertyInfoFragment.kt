package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import kotlinx.android.synthetic.main.fragment_add_info.*

class AddPropertyInfoFragment : BaseEditPropertyFragment() {
    companion object {
        const val TAG = "AddInfoFragment"
    }

    override fun getLayout() = R.layout.fragment_add_info

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onStart: ")
        addPropertyActivity.intent.extras?.let {
            if (price_input.text.isEmpty() && updatedProperty.price.toString().isNotEmpty())
                bindInputs()
        }
        setInputListeners()
    }

    private fun setInputListeners() {
        types_spinner.onItemSelectedListener = getTypeListener()
        price_input.addTextChangedListener(getPriceTextWatcher())
        nbr_of_beds_input.addTextChangedListener(getNbrOfBedTextWatcher())
        nbr_of_bath_input.addTextChangedListener(getNbrOfBathTextWatcher())
        surface_input.addTextChangedListener(getSurfaceTextWatcher())
        availability_spinner.onItemSelectedListener = getAvailabilityListener()
        description_input.addTextChangedListener(getDescriptionTextWatcher())
    }

    private fun bindInputs() {
        types_spinner.setSelection(getSpinnerPosition(updatedProperty.type.s, R.array.property_types))
        price_input.text.insert(0, updatedProperty.price.toString())
        nbr_of_beds_input.text.insert(0, updatedProperty.numberOfBedrooms.toString())
        nbr_of_bath_input.text.insert(0, updatedProperty.numberOfBathrooms.toString())
        surface_input.text.insert(0, updatedProperty.surface.toString())
        availability_spinner.setSelection(getSpinnerPosition(updatedProperty.availability.s, R.array.property_availability))
        description_input.text.insert(0, updatedProperty.description)
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }


    //-- Listeners/TextWatchers --//
    private fun getTypeListener() = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updatedProperty.type = PropertyType.values()[position]
        }
    }

    private fun getDescriptionTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updatedProperty.description = s.toString()
        }
    }

    private fun getPriceTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s != null && s.isNotEmpty())
                updatedProperty.price = s.toString().toInt()
        }
    }

    private fun getNbrOfBedTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updatedProperty.numberOfBedrooms = s.toString().toInt()
        }
    }

    private fun getNbrOfBathTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updatedProperty.numberOfBathrooms = s.toString().toInt()
        }
    }

    private fun getSurfaceTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            updatedProperty.surface = s.toString().toInt()
        }
    }

    private fun getAvailabilityListener() = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updatedProperty.availability = PropertyAvailability.values()[position]
        }
    }
}