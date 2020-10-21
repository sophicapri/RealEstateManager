package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentPropertyListBinding
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


open class PropertyListFragment : BaseFragment(), PropertyListAdapter.OnPropertyClickListener {
    lateinit var adapter: PropertyListAdapter
    private val propertyViewModel by viewModel<PropertyViewModel>()
    private lateinit var binding: FragmentPropertyListBinding

    companion object{
        const val TAG = "LogPropertyListFragment"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_property_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView(binding.recyclerViewProperties)
        propertyViewModel.getProperties().observe(mainContext, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    adapter.updateList(ArrayList(it))
                    binding.noPropertiesInDb.visibility = GONE
                } else
                    binding.noPropertiesInDb.visibility = VISIBLE
            }
        })
    }

    fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyListAdapter(this)
        recyclerView.adapter = adapter
    }

    fun updateList(filteredList: ArrayList<Property>){
        adapter.updateList(filteredList)
    }

    fun resetFilter(){
        onResume()
    }

    override fun onPropertyClick(propertyId: String) {
        val propertyDetailView = activity?.findViewById<View?>(R.id.frame_property_details)
        if (propertyDetailView == null) {
            val intent = Intent(mainContext, PropertyDetailActivity::class.java)
            intent.putExtra(PROPERTY_ID, propertyId)
            mainContext.startActivityForResult(intent, RQ_CODE_PROPERTY)
        } else {
            val bundle = Bundle()
            bundle.putString(PROPERTY_ID, propertyId)
            val propertyDetailFragment = PropertyDetailFragment()
            propertyDetailFragment.arguments = bundle
            mainContext.supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_property_details, propertyDetailFragment).commit()
        }
    }

    override fun getLayout() = Pair(null, binding.root)
}