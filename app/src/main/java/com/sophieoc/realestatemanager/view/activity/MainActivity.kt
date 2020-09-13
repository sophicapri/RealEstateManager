package com.sophieoc.realestatemanager.view.activity

import android.os.Bundle
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.base.BaseActivity


class MainActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_main
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}