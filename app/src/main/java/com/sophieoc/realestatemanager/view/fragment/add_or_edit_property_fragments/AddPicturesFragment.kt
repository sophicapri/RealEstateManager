package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.util.Log
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseEditPropertyFragment
import com.sophieoc.realestatemanager.model.Photo
import com.sophieoc.realestatemanager.view.adapter.PicturesAdapter
import kotlinx.android.synthetic.main.fragment_add_pictures.*

class AddPicturesFragment : BaseEditPropertyFragment(), PicturesAdapter.OnDeletePictureListener {
    private lateinit var adapter: PicturesAdapter

    override fun getLayout() = R.layout.fragment_add_pictures

    override fun onResume() {
        super.onResume()
        bindViews()
        configureRecyclerView(updatedProperty.photos)
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
        updatedProperty.photos = pictures
    }
}