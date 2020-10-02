package com.sophieoc.realestatemanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTY_KEY
import com.sophieoc.realestatemanager.utils.RQ_CODE_ADD_PROPERTY
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.view.PropertyListAdapter
import com.sophieoc.realestatemanager.view.activity.AddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.MainActivity
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import kotlinx.android.synthetic.main.fragment_property_list.*


class PropertyListFragment : BaseFragment(), PropertyListAdapter.OnPropertyClickListener {
    private lateinit var adapter: PropertyListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProperties().observe(mainContext, Observer {
            if (it != null) {
                adapter.updateList(ArrayList(it))
            }
        })
        configureRecyclerView()
        fab_add_property.setOnClickListener {
            startAddPropertyActivity()
        }
    }

    private fun startAddPropertyActivity() {
        val intent = Intent(mainContext, AddPropertyActivity::class.java)
        mainContext.startActivityForResult(intent, RQ_CODE_ADD_PROPERTY)
    }

    private fun configureRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        recycler_view_properties.setHasFixedSize(true)
        recycler_view_properties.layoutManager = linearLayoutManager
        adapter = PropertyListAdapter(this, Glide.with(this))
        recycler_view_properties.adapter = adapter
    }

    override fun onPropertyClick(propertyId: String) {
        val propertyDetailView = activity?.findViewById<View?>(R.id.frame_property_details)
        if (propertyDetailView == null) {
            val intent = Intent(mainContext, PropertyDetailActivity::class.java)
            intent.putExtra(PROPERTY_KEY, propertyId)
            mainContext.startActivityForResult(intent, RQ_CODE_PROPERTY)
        } else {
            val bundle = Bundle()
            bundle.putString(PROPERTY_KEY, propertyId)
            val propertyDetailFragment = PropertyDetailFragment()
            propertyDetailFragment.arguments = bundle
            mainContext.supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_property_details, propertyDetailFragment).commit()
        }
    }

    override fun getLayout() = R.layout.fragment_property_list

}