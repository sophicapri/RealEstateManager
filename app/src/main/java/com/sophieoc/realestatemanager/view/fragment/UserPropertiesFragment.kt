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
import com.sophieoc.realestatemanager.databinding.FragmentUserPropertiesBinding
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.USER_ID
import com.sophieoc.realestatemanager.view.activity.UserPropertiesActivity
import com.sophieoc.realestatemanager.view.adapter.PropertyListAdapter
import com.sophieoc.realestatemanager.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserPropertiesFragment : Fragment() {
    private val userViewModel by viewModel<UserViewModel>()
    private lateinit var adapter: PropertyListAdapter
    private lateinit var binding: FragmentUserPropertiesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_properties, container, false)
        binding.lifecycleOwner = this
        bindViews()
        return binding.root
    }

    private fun bindViews() {
        val id = if (requireActivity().intent.hasExtra(USER_ID))
            requireActivity().intent.extras?.get(USER_ID) as String
        else
            PreferenceHelper.currentUserId
        if (id.isNotEmpty()) {
            binding.apply {
                userViewModel.getUserById(id).observe(requireActivity(), {
                    if (it != null) {
                            user = it.user
                            myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
                            configureRecyclerView(recyclerViewUserProperties)
                            adapter.updateList(ArrayList(it.properties))
                    }
                    if (id != PreferenceHelper.currentUserId)
                        myToolbar.title =
                            activity?.getString(R.string.agent_properties, it.user.username)
                    else
                        myToolbar.title = activity?.getString(R.string.my_properties)
                })
            }
        }
    }

    private fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PropertyListAdapter(getListener())
        recyclerView.adapter = adapter
    }

    private fun getListener() = object : PropertyListAdapter.OnPropertyClickListener {
        override fun onPropertyClick(propertyId: String) {
            (requireActivity() as UserPropertiesActivity).onPropertyClick(propertyId)
        }
    }
}