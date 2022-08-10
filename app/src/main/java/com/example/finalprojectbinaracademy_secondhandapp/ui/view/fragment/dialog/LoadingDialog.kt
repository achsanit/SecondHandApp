package com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.example.finalprojectbinaracademy_secondhandapp.R

class LoadingDialog(context: Context) {

    private var dialog: Dialog

    init {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_loading_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.create()
    }

    fun startLoading() {
        dialog.show()
    }

    fun dismissLoading() {
        dialog.dismiss()
    }
}