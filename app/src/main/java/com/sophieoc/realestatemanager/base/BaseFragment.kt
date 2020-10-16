package com.sophieoc.realestatemanager.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.fragment.app.Fragment
import com.sophieoc.realestatemanager.utils.Utils
import com.sophieoc.realestatemanager.utils.toDate
import com.sophieoc.realestatemanager.viewmodel.MyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

abstract class BaseFragment: Fragment()  {
    val viewModel by viewModel<MyViewModel>()
    val today = Utils.todayDate.toDate()
    lateinit var mainContext: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        mainContext = activity as BaseActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View? = null
        getLayout().first?.let { view = inflater.inflate(it, container, false) }
        getLayout().second?.let { view = getLayout().second }
        return view
    }

    abstract fun getLayout(): Pair<Int?, View?>
}