package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
import com.sophieoc.realestatemanager.databinding.FragmentAddAddressBinding
import com.sophieoc.realestatemanager.databinding.FragmentAddPicturesBinding
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_add_pictures.*

class AddPicturesFragment : BaseFragment(), PicturesAdapter.OnDeletePictureListener {
    lateinit var binding : FragmentAddPicturesBinding
    private lateinit var adapter: PicturesAdapter
    private lateinit var addPropertyActivity : EditOrAddPropertyActivity

    override fun getLayout(): Pair<Nothing?, View> = Pair(null, binding.root)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_add_pictures,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.propertyViewModel = addPropertyActivity.propertyViewModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun onResume() {
        super.onResume()
        bindViews()
        configureRecyclerView(addPropertyActivity.propertyViewModel.property.photos)
    }

    private fun bindViews() {
        btn_add_picture.setOnClickListener { }
    }

    private fun configureRecyclerView(photos: List<Photo>) {
        recycler_view_pictures.setHasFixedSize(true)
        recycler_view_pictures.layoutManager = LinearLayoutManager(context)
        adapter = PicturesAdapter(Glide.with(this), this)
        adapter.updatePictures(ArrayList(photos))
        recycler_view_pictures.adapter = adapter
    }

    override fun onDeleteClick(position: Int, pictures: ArrayList<Photo>) {
        pictures.removeAt(position)
        addPropertyActivity.propertyViewModel.property.photos = pictures
    }
}