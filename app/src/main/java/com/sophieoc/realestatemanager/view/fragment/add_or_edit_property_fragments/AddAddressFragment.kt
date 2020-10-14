package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentAddAddressBinding
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity


class AddAddressFragment : BaseFragment(){
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var addPropertyActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
        addPropertyActivity.intent.extras?.let {
            getPropertyId(it)
        }
        if (addPropertyActivity.intent.extras == null)
            addPropertyActivity.propertyViewModel.property = Property()
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
                addPropertyActivity.propertyViewModel.property = it
                val ft: FragmentTransaction = mainContext.supportFragmentManager.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
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
        binding.propertyViewModel = addPropertyActivity.propertyViewModel
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun getLayout(): Pair<Nothing?, View> {
        return Pair(null, binding.root)
    }

    companion object {
        const val TAG = "AddAddressFragment"
    }
}