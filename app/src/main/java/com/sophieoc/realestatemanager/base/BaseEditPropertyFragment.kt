package com.sophieoc.realestatemanager.base

import android.os.Bundle
import android.util.Log
import com.sophieoc.realestatemanager.model.Property
import com.sophieoc.realestatemanager.view.activity.EditOrAddPropertyActivity

abstract class BaseEditPropertyFragment : BaseFragment(){
    lateinit var addPropertyActivity: EditOrAddPropertyActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPropertyActivity = (activity as EditOrAddPropertyActivity)
    }

    companion object {
        var updatedProperty = Property()
        const val TAG = "BaseEditFragment"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}