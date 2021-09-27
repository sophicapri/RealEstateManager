package com.sophieoc.realestatemanager.presentation.ui.userproperty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.FragmentUserPropertiesBinding
import com.sophieoc.realestatemanager.presentation.ui.UserViewModel
import com.sophieoc.realestatemanager.presentation.ui.propertylist.PropertyListAdapter
import com.sophieoc.realestatemanager.utils.PreferenceHelper
import com.sophieoc.realestatemanager.utils.USER_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
        configureRecyclerView(binding.recyclerViewUserProperties)
        val id = if (requireActivity().intent.hasExtra(USER_ID))
            requireActivity().intent.extras?.get(USER_ID) as String
        else
            PreferenceHelper.currentUserId
        if (id.isNotEmpty()) {
                lifecycleScope.launchWhenStarted {
                userViewModel.getUserById(id).collect { userUiState ->
                    when (userUiState) {
                        is UserUiState.Success -> {
                                binding.apply {
                                    user = userUiState.userWithProperties.user
                                    myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }
                                    adapter.updateList(ArrayList(userUiState.userWithProperties.properties))
                                    if (id != PreferenceHelper.currentUserId)
                                        myToolbar.title = activity?.getString(R.string.agent_properties,
                                                userUiState.userWithProperties.user.username)
                                    else
                                        myToolbar.title = activity?.getString(R.string.my_properties)
                                }
                            }
                        is UserUiState.Error -> {/*TODO:*/}
                        is UserUiState.Loading -> {/*TODO:*/}
                    }
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}