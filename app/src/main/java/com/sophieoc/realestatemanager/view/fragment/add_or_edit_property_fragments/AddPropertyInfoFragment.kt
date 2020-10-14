package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentAddInfoBinding
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import kotlinx.android.synthetic.main.fragment_add_info.*

class AddPropertyInfoFragment : BaseFragment() {
    lateinit var binding : FragmentAddInfoBinding
    private lateinit var addPropertyActivity : EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun getLayout(): Pair<Int, Nothing?> {
        return Pair(R.layout.fragment_add_info, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_info,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = addPropertyActivity.propertyViewModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        bindViews()
    }

    private fun bindViews() {
        val property = addPropertyActivity.propertyViewModel.property
        types_spinner.setSelection(getSpinnerPosition(property.type.s, R.array.property_types))
        availability_spinner.setSelection(getSpinnerPosition(property.availability.s, R.array.property_availability))
    }

    private fun getSpinnerPosition(value: String, array: Int): Int {
        val arrayList = resources.getStringArray(array)
        var position = -1
        arrayList.forEachIndexed { index, s -> if (s == value) position = index }
        return position
    }

    companion object {
        const val TAG = "AddInfoFragment"
    }
}
