package com.msc.ar_drawing.component.drawing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityMain1Binding
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.ViewEx.gone
import com.msc.ar_drawing.utils.ViewEx.invisible
import com.msc.ar_drawing.utils.ViewEx.textColorRes
import com.msc.ar_drawing.utils.ViewEx.tintColorRes
import com.msc.ar_drawing.utils.ViewEx.visible
import java.util.concurrent.Executors

class DrawingActivity : BaseActivity<ActivityMain1Binding>() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private var isFlip = false

    companion object {

        private const val KEY_DRAWING_MODE = "KEY_DRAWING_MODE"
        private const val KEY_ASSET_TYPE = "KEY_ASSET_TYPE"
        private const val KEY_TEXT = "KEY_TEXT"
        private const val KEY_COLOR_TEXT = "KEY_COLOR_TEXT"

        const val SKETCH_DRAWING_MODE = 1;
        const val TRACE_DRAWING_MODE = 2;
        private const val BITMAP_ASSET = 3;
        const val TEXT_ASSET = 4;

        var currentBitmapSticker : Bitmap? = null
        var currentTypeface : Typeface? = null

        fun startWithBitmap(activity: Activity, drawMode : Int, bitmap: Bitmap){
            val intent = Intent(activity, DrawingActivity::class.java )
            intent.putExtra(KEY_DRAWING_MODE, drawMode)
            intent.putExtra(KEY_ASSET_TYPE, BITMAP_ASSET)

            currentBitmapSticker = bitmap

            activity.startActivity(intent)
        }

        fun startWithText(activity: Activity, drawMode: Int, text : String, color : Int, typeface: Typeface?){
            val intent = Intent(activity, DrawingActivity::class.java)
            intent.putExtra(KEY_DRAWING_MODE, drawMode)
            intent.putExtra(KEY_ASSET_TYPE, TEXT_ASSET)
            intent.putExtra(KEY_COLOR_TEXT, color)
            intent.putExtra(KEY_TEXT, text)

            currentTypeface = typeface

            activity.startActivity(intent)
        }
    }

    override fun provideViewBinding(): ActivityMain1Binding {
        return ActivityMain1Binding.inflate(layoutInflater)
    }

    override fun initViews() {
        if(PermissionUtils.cameraGrant(this)){
//            startCamera()
        }else{
            PermissionUtils.requestCamera(this, 322)
        }

        viewBinding.run {
            opaciy.setOnClickListener {
                showMenu(imvOpacity, tvOpacity, menuOpacity)
            }
            picture.setOnClickListener {
                showMenu(imvPicture, tvPicture, menuPicture)
            }
            flash.setOnClickListener {
                showToast("flash")
            }
            record.setOnClickListener {
                showToast("record")
            }
            flip.setOnClickListener {
                flipImage()
            }

            sliderOpacity.addOnChangeListener(Slider.OnChangeListener { _, value, _ -> onChangeOpacity(value); })
        }

        checkDataIntent()
    }

    private fun checkDataIntent() {
        viewBinding.run {
            intent.extras?.let { extras ->
                val drawMode = extras.getInt(KEY_DRAWING_MODE)
                val assetType = extras.getInt(KEY_ASSET_TYPE)

                when(drawMode){
                    SKETCH_DRAWING_MODE -> {

                    }

                    TRACE_DRAWING_MODE -> {

                    }
                }

                when(assetType){
                    BITMAP_ASSET -> {
                        imvSticker.visible()
                        tvSticker.invisible()
                        Glide.with(this@DrawingActivity).load(currentBitmapSticker).into(imvSticker)
                    }

                    TEXT_ASSET -> {
                        tvSticker.visible()
                        imvSticker.invisible()
                        setDatText(tvSticker, extras)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun setDatText(tvSticker: TextView, extras: Bundle) {
        val text = extras.getString(KEY_TEXT)
        val color = extras.getInt(KEY_COLOR_TEXT)

        tvSticker.text = text
        tvSticker.setTextColor(color)
        tvSticker.typeface = currentTypeface
    }

    private fun flipImage() {
        viewBinding.run {
            isFlip = !isFlip
            if(isFlip){
                imvFlip.tintColorRes(R.color.app_main)
                tvFlip.textColorRes(R.color.app_main)
                imvSticker.animate().scaleX(-1f).setDuration(0).start()
            }else{
                imvFlip.tintColorRes(R.color.gray)
                tvFlip.textColorRes(R.color.gray)
                imvSticker.animate().scaleX(1f).setDuration(0).start()
            }

        }
    }

    private fun onChangeOpacity(value: Float) {
        viewBinding.run {
            imvSticker.animate().alpha(value).setDuration(0).start()
            tvSticker.animate().alpha(value).setDuration(0).start()
            tvOpacityPercent.text = (value * 100).toInt().toString() + "%"
        }
    }

    private fun showMenu(imv: ImageView, tv: TextView, menu: LinearLayout) {


        viewBinding.run {
            imvPicture.tintColorRes(R.color.gray)
            tvPicture.textColorRes(R.color.gray)

            imvOpacity.tintColorRes(R.color.gray)
            tvOpacity.textColorRes(R.color.gray)

            menuPicture.gone()
            menuOpacity.gone()
        }

        menu.visible()

        imv.tintColorRes(R.color.app_main)
        tv.textColorRes(R.color.app_main)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder()
                .build()
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(viewBinding.preview.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
//                        viewModel.process(imageProxy)
                    }
                }

            try {
                preview.setSurfaceProvider(viewBinding.preview.surfaceProvider)
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer, preview)
//                val cameraControl = camera?.cameraControl
//                cameraControl?.enableTorch(true)
//                registerCameraFlashStatus()
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }
}