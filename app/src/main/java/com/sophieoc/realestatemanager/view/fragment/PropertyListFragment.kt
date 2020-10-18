package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.utils.PROPERTY_ID
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.RQ_CODE_PROPERTY
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.activity.PropertyDetailActivity
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.viewmodel.PropertyViewModel
import kotlinx.android.synthetic.main.fragment_property_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel


open class PropertyListFragment : BaseFragment(), PropertyListAdapter.OnPropertyClickListener {
    lateinit var adapter: PropertyListAdapter
    val propertyViewModel by viewModel<PropertyViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView(recycler_view_properties)
        fab_add_property.setOnClickListener {
            startAddPropertyActivity()
        }
    }

    private fun startAddPropertyActivity() {
        if (Utils.isConnectionAvailable(mainContext)) {
            mainContext.startNewActivity(EditOrAddPropertyActivity::class.java)
            PreferenceHelper.internetAvailable = true
        } else {
            Toast.makeText(mainContext, getString(R.string.edit_add_unavailable), Toast.LENGTH_LONG).show()
            PreferenceHelper.internetAvailable = false
        }
    }

    override fun onResume() {
        super.onResume()
        propertyViewModel.getProperties().observe(mainContext, {
            if (it != null) {
                adapter.updateList(ArrayList(it))
            }
        })
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
        onResume()
    }

    //todo : handle fragment change
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

    override fun getLayout() = Pair(R.layout.fragment_property_list, null)
}