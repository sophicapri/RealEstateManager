package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentAddAddressBinding
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity


class AddAddressFragment : Fragment(){
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var rootActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootActivity = (activity as EditOrAddPropertyActivity)
        rootActivity.intent.extras?.let {
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
        rootActivity.propertyViewModel.getPropertyById(propertyId).observe(rootActivity, {
            it?.let {
                rootActivity.propertyViewModel.property = it
                // to update the view
                val ft: FragmentTransaction = rootActivity.supportFragmentManager.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false)
                }
                ft.detach(this).attach(this)
                        .detach(rootActivity.fragmentPropertyInfo).attach(rootActivity.fragmentPropertyInfo)
                        .detach(rootActivity.fragmentPictures).attach(rootActivity.fragmentPictures).commit()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_address,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = rootActivity.propertyViewModel
        if (rootActivity.activityRestarted)
            binding.executePendingBindings()
        return binding.root
    }

    companion object {
        const val TAG = "AddAddressFragment"
    }
}