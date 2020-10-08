package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import kotlinx.android.synthetic.main.fragment_property_list.*


open class PropertyListFragment : BaseFragment(), PropertyListAdapter.OnPropertyClickListener {
    lateinit var adapter: PropertyListAdapter
    var properties = ArrayList<Property>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getProperties().observe(mainContext, Observer {
            if (it != null) {
                properties = ArrayList(it)
                adapter.updateList(properties)
            }
        })
        configureRecyclerView(recycler_view_properties)
        fab_add_property.setOnClickListener {
            mainContext.startNewActivity(EditOrAddPropertyActivity::class.java)
        }
    }

    fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyListAdapter(this, Glide.with(this))
        recyclerView.adapter = adapter
    }

    fun updateList(filteredList: ArrayList<Property>){
        adapter.updateList(filteredList)
    }

    fun resetFilter(){
        adapter.updateList(properties)
        // todo :hide search title
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

    override fun getLayout() = R.layout.fragment_property_list
}