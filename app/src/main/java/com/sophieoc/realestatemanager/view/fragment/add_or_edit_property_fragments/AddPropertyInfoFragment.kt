package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentAddInfoBinding
import com.sophieoc.realestatemanager.utils.PropertyAvailability
import com.sophieoc.realestatemanager.utils.PropertyType
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import kotlinx.android.synthetic.main.fragment_add_info.*

class AddPropertyInfoFragment : BaseFragment() {
    lateinit var binding : FragmentAddInfoBinding
    private lateinit var addPropertyActivity : EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }


    override fun getLayout() = Pair(null, binding.root)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_info,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = addPropertyActivity.propertyViewModel
        if (addPropertyActivity.activityRestarted)
            binding.executePendingBindings()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if(addPropertyActivity.emptyFieldsInMainInfo){
            addPropertyActivity.checkInputs(addPropertyActivity.propertyViewModel.property)
        }
        bindViews()
    }

    private fun bindViews() {
        val property = addPropertyActivity.propertyViewModel.property
        types_spinner.setSelection(getSpinnerPosition(property.type.s, R.array.property_types))
        types_spinner.onItemSelectedListener = getOnTypeSelectedListener()
        availability_spinner.setSelection(getSpinnerPosition(property.availability.s, R.array.property_availability))
        availability_spinner.onItemSelectedListener = getOnAvailabilitySelectedListener()
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }

    private fun getOnAvailabilitySelectedListener()= object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            addPropertyActivity.propertyViewModel.property.availability = PropertyAvailability.values()[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun getOnTypeSelectedListener() = object : AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            addPropertyActivity.propertyViewModel.property.type = PropertyType.values()[position]
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    companion object {
        const val TAG = "AddInfoFragment"
    }
}
