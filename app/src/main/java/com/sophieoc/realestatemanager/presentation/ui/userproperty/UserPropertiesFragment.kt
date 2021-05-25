package com.sophieoc.realestatemanager.presentation.ui.userproperty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentUserPropertiesBinding
import com.sophieoc.realestatemanager.presentation.ui.UserViewModel
import com.sophieoc.realestatemanager.presentation.ui.propertylist.PropertyListAdapter
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.USER_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPropertiesFragment : Fragment() {
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var adapter: PropertyListAdapter
    private var _binding: FragmentUserPropertiesBinding? = null
    private val binding: FragmentUserPropertiesBinding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_properties, container, false)
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