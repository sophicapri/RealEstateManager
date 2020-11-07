package com.sophieoc.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity
import com.sophieoc.realestatemanager.databinding.FragmentUserPropertiesBinding
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.USER_ID
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserPropertiesFragment : Fragment() {
    private val userViewModel by viewModel<UserViewModel>()
    private lateinit var adapter: PropertyListAdapter
    private lateinit var mainContext: BaseActivity
    private lateinit var binding: FragmentUserPropertiesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_properties, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainContext = activity as BaseActivity
        var id = ""
        if (mainContext.intent.hasExtra(USER_ID))
            id = mainContext.intent.extras?.get(USER_ID) as String
        if (id.isNotEmpty()) {
            userViewModel.getUserById(id).observe(mainContext, {
                if (it != null) initView(it)
                if (id != PreferenceHelper.currentUserId)
                    binding.myToolbar.title = mainContext.getString(R.string.agent_properties, it.user.username)
                else
                    binding.myToolbar.title = mainContext.getString(R.string.my_properties)
            })
        }
    }

    private fun initView(it: UserWithProperties) {
        binding.user = it.user
        binding.myToolbar.setNavigationOnClickListener { mainContext.onBackPressed() }
        configureRecyclerView(binding.recyclerViewUserProperties)
        adapter.updateList(ArrayList(it.properties))
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
}