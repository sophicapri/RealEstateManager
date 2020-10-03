package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_user_properties.*

class UserPropertiesFragment: PropertyListFragment() {
    override fun getLayout() = R.layout.fragment_user_properties

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.currentUser.observe(mainContext, Observer {
            if (it != null) {
                adapter.updateList(ArrayList(it.properties))
            }
        })
        configureRecyclerView(recycler_view_user_properties)
    }
}