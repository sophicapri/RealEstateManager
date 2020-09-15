package com.sophieoc.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.view.activity.MainActivity
import com.sophieoc.realestatemanager.view.activity.MapActivity
import kotlinx.android.synthetic.main.fragment_property_list.*

class PropertyListFragment: BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        open_map_activity.setOnClickListener {
                val intent = Intent(activity, MapActivity::class.java)
                startActivity(intent)
        }
    }

    override fun getLayout() = R.layout.fragment_property_list
}