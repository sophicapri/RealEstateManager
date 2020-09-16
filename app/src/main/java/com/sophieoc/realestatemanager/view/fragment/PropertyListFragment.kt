package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.view.PropertyListAdapter
import kotlinx.android.synthetic.main.fragment_property_list.*

class PropertyListFragment: BaseFragment(), PropertyListAdapter.OnPropertyClickListener {
    private lateinit var adapter: PropertyListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProperties().observe(mainContext, Observer {
            if (it != null) {
                adapter.updateList(ArrayList(it))
            }
        })

        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        recycler_view_properties.setHasFixedSize(true)
        recycler_view_properties.layoutManager = linearLayoutManager
        adapter = PropertyListAdapter(this , Glide.with(this))
        recycler_view_properties.adapter = adapter
    }

    override fun onPropertyClick(propertyId: String) {
        //to-do: display property detail fragment
    }

    override fun getLayout() = R.layout.fragment_property_list

}