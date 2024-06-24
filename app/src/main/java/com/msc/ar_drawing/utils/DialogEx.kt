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
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

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
                reLoad()
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

    fun Activity?.showDialogApplyText(sketchAction : (() -> Unit)? = null, traceAction : (() -> Unit)? = null){
        this?.let {activity ->
            val builder = AlertDialog.Builder(this)
            val binding = DialogApplyTextBinding.inflate(LayoutInflater.from(this))
            builder.setView(binding.root)
            builder.show()

            binding.run {
                sketch.setOnClickListener {
                    sketchAction?.invoke()
                }
                trace.setOnClickListener {
                    traceAction?.invoke()
                }
            }
        }
    }

    fun Activity.showPickerColor(pickedAction : ((Int) -> Unit)? = null){
        ColorPickerDialog.Builder(this)
            .setTitle("ColorPicker Dialog")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Select",
                ColorEnvelopeListener { envelope, fromUser ->
                    val color = envelope.color
                    pickedAction?.invoke(color)
                })
            .setNegativeButton(this.getString(android.R.string.cancel)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            .show()
    }
}