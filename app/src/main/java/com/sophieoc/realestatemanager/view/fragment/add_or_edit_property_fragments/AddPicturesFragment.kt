package com.sophieoc.realestatemanager.view.fragment.add_or_edit_property_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseFragment
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
        if (addPropertyActivity.activityRestarted) {
            binding.executePendingBindings()
        }
        Log.d(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    override fun onResume() {
        super.onResume()
        bindViews()
        configureRecyclerView()
    }

    private fun bindViews() {
        btn_add_picture.setOnClickListener { }
    }

    private fun configureRecyclerView() {
        recycler_view_pictures.setHasFixedSize(true)
        adapter = PicturesAdapter(this, addPropertyActivity.propertyViewModel)
        recycler_view_pictures.adapter = adapter
    }

    override fun onDeleteClick(position: Int, photos: List<Photo>) {
        val photosArray : ArrayList<Photo> = ArrayList(photos)
        photosArray.removeAt(position)
        addPropertyActivity.propertyViewModel.property.photos = photosArray
    }

    companion object{
        const val TAG = "AddPictures"
    }
}