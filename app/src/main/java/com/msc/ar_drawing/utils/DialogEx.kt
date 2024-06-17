package com.msc.ar_drawing.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.msc.ar_drawing.R
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.databinding.DialogApplyTextBinding
import com.msc.ar_drawing.databinding.DialogExitBinding
import com.msc.ar_drawing.utils.DialogEx.showDialogExit

object DialogEx {

    fun Activity?.showDialogExit(
        lifecycle: LifecycleOwner,
        exitAction : (() -> Unit) ? = null
    ){
        this?.let {activity ->
            val builder = BottomSheetDialog(this)
            val binding = DialogExitBinding.inflate(LayoutInflater.from(this))
            builder.setContentView(binding.root)
            builder.show()

            NativeAdmobUtils.nativeExitLiveData?.run {
                nativeAdLive?.observe(lifecycle){
                    if(available() && SpManager.getInstance(activity).getBoolean(NameRemoteAdmob.NATIVE_EXIT, true)){
                        binding.flAdplaceholder.visibility = View.VISIBLE
                        showNative(binding.flAdplaceholder, null)
                    }
                }
            }

            with(binding){
                tvExit.setOnClickListener {
                    exitAction?.invoke()
                }
                tvCancel.setOnClickListener {
                    builder.dismiss()
                    NativeAdmobUtils.nativeExitLiveData?.reLoad()
                }
            }
        }
    }

    fun Activity?.showDialogApplyText(){
        this?.let {activity ->
            val builder = AlertDialog.Builder(this)
            val binding = DialogApplyTextBinding.inflate(LayoutInflater.from(this))
            builder.setView(binding.root)
            builder.show()
        }
    }
}