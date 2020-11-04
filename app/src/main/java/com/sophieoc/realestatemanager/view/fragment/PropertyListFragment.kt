package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.FragmentPropertyListBinding
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.MainActivity
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PropertyListFragment : Fragment() {
    private lateinit var adapter: PropertyListAdapter
    private lateinit var mainContext: BaseActivity
    private val propertyViewModel by viewModel<PropertyViewModel>()
    private lateinit var binding: FragmentPropertyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainContext = activity as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_property_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView(binding.recyclerViewProperties)
        binding.swipeRefreshView.setOnRefreshListener {
            if (binding.resultsSearchContainer.visibility == VISIBLE) {
                (mainContext as MainActivity).displayResults()
            } else
                getAndUpdatePropertiesList()
            binding.swipeRefreshView.isRefreshing = false
        }
        binding.fabAddProperty.setOnClickListener {
            (mainContext as MainActivity).startNewActivity(EditOrAddPropertyActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        mainContext.checkConnection()
        if (binding.resultsSearchContainer.visibility == VISIBLE)
            (mainContext as MainActivity).displayResults()
        else
            getAndUpdatePropertiesList()
    }

    private fun getAndUpdatePropertiesList() {
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

    private fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyListAdapter(getListener())
        recyclerView.adapter = adapter
    }

    private fun getListener() = object : PropertyListAdapter.OnPropertyClickListener {
        override fun onPropertyClick(propertyId: String) {
            mainContext.onPropertyClick(propertyId)
        }
    }

    fun updateList(filteredList: ArrayList<Property>) {
        adapter.updateList(filteredList)
    }

    fun resetFilter() {
        getAndUpdatePropertiesList()
    }
}