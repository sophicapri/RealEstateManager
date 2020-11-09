package com.sophieoc.realestatemanager.utils

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sophieoc.realestatemanager.R
import com.sophieoc.realestatemanager.databinding.BottomSheetDialogBinding

class CustomBottomSheetDialog(context: Context, theme: Int) : BottomSheetDialog(context, theme){
    private lateinit var bindingBottomSheet: BottomSheetDialogBinding

    fun buildBottomSheetDialog(): CustomBottomSheetDialog {
        this.setContentView(getBinding().root)
        this.setCanceledOnTouchOutside(true)
        return this
    }

    fun getBinding(): BottomSheetDialogBinding {
        val inflater = layoutInflater
        if (!::bindingBottomSheet.isInitialized)
            bindingBottomSheet = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_dialog, null, false)
        return bindingBottomSheet
    }
}