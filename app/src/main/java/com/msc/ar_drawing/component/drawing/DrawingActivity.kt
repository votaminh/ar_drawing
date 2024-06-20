package com.msc.ar_drawing.component.drawing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface.ROTATION_0
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.text.AddTextViewModel
import com.msc.ar_drawing.component.text.ColorAdapter
import com.msc.ar_drawing.databinding.ActivityMain1Binding
import com.msc.ar_drawing.utils.AppEx.replaceWhiteToTransparentBitmap
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.DialogEx.showPickerColor
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.ViewEx.gone
import com.msc.ar_drawing.utils.ViewEx.invisible
import com.msc.ar_drawing.utils.ViewEx.textColorRes
import com.msc.ar_drawing.utils.ViewEx.tintColorRes
import com.msc.ar_drawing.utils.ViewEx.visible
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@AndroidEntryPoint
class DrawingActivity : BaseActivity<ActivityMain1Binding>() {

    private var assetType: Int = BITMAP_ASSET
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private val colorAdapter = ColorAdapter()

    private var isFlip = false
    private var isFlash = false

    private val viewModel : AddTextViewModel by viewModels()

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
        var currentSketchBitmapSticker : Bitmap? = null
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

        viewBinding.run {
            opaciy.setOnClickListener {
                showMenu(imvOpacity, tvOpacity, menuOpacity)
            }
            picture.setOnClickListener {
                if(assetType == BITMAP_ASSET){
                    showMenu(imvPicture, tvPicture, menuPicture)
                }else{
                    showToast(getString(R.string.txt_not_support_in_text))
                }
            }
            flash.setOnClickListener {
                turnFlash()
            }
            record.setOnClickListener {
                showToast("record")
            }
            flip.setOnClickListener {
                flipImage()
            }
            line.setOnClickListener {
                showMenu(imvLineColor, tvLineColor, menuLineColor)
            }

            originalImage.setOnClickListener {
                originalImage.setBackgroundResource(R.drawable.bg_main_round_stroke)
                sketchImage.setBackgroundResource(R.drawable.bg_black_round_stroke)
                originalImage.textColorRes(R.color.app_main)
                sketchImage.textColorRes(R.color.black)
                Glide.with(this@DrawingActivity).load(currentBitmapSticker).into(imvSticker)
            }

            sketchImage.setOnClickListener {
                sketchImage.setBackgroundResource(R.drawable.bg_main_round_stroke)
                originalImage.setBackgroundResource(R.drawable.bg_black_round_stroke)
                sketchImage.textColorRes(R.color.app_main)
                originalImage.textColorRes(R.color.black)
                Glide.with(this@DrawingActivity).load(currentSketchBitmapSticker).into(imvSticker)
            }

            sliderOpacity.addOnChangeListener(Slider.OnChangeListener { _, value, _ -> onChangeOpacity(value); })
        }

        checkDataIntent()

        buildReColor()
        viewModel.getColors()
    }

    private fun turnFlash() {
        viewBinding.run {
            if(isFlash){
                isFlash = false
                imvFlash.tintColorRes(R.color.gray)
                tvFlash.textColorRes(R.color.gray)
            }else{
                isFlash = true
                imvFlash.tintColorRes(R.color.app_main)
                tvFlash.textColorRes(R.color.app_main)
            }
        }

        val cameraControl = camera?.cameraControl
        cameraControl?.enableTorch(isFlash)
    }

    override fun initObserver() {
        super.initObserver()

        viewModel.run {
            colorsLive.observe(this@DrawingActivity){
                colorAdapter.setData(it)
            }
        }
    }

    private fun buildReColor() {
        viewBinding.reColor.run {
            layoutManager = LinearLayoutManager(this@DrawingActivity, RecyclerView.HORIZONTAL, false)
            adapter = colorAdapter
            colorAdapter.onClickWithPosition = { it, i ->
                if(it == -1){
                    showPickerColor{
                        viewBinding.maskBgTrace.setBackgroundColor(it)
                        colorAdapter.setSelectItem(i)
                    }
                }else{
                    viewBinding.maskBgTrace.setBackgroundColor(it)
                    colorAdapter.setSelectItem(i)
                }
            }
        }
    }

    private fun checkDataIntent() {
        viewBinding.run {
            intent.extras?.let { extras ->
                val drawMode = extras.getInt(KEY_DRAWING_MODE)
                assetType = extras.getInt(KEY_ASSET_TYPE)

                when(drawMode){
                    SKETCH_DRAWING_MODE -> {
                        line.gone()
                        flash.visible()
                        maskBgTrace.gone()

                        if(PermissionUtils.cameraGrant(this@DrawingActivity)){
                            startCamera()
                        }else{
                            PermissionUtils.requestCamera(this@DrawingActivity, 322)
                        }
                    }

                    TRACE_DRAWING_MODE -> {
                        line.visible()
                        flash.gone()
                        maskBgTrace.visible()
                    }
                }

                when(assetType){
                    BITMAP_ASSET -> {
                        imvSticker.visible()
                        tvSticker.invisible()
                        Glide.with(this@DrawingActivity).load(currentBitmapSticker).into(imvSticker)
                        val gpuimageview = GPUImage(this@DrawingActivity)
                        gpuimageview.run {
                            setImage(currentBitmapSticker)
                            setFilter(GPUImageSketchFilter())
                            CoroutineScope(Dispatchers.IO).launch {
                                currentSketchBitmapSticker = replaceWhiteToTransparentBitmap(bitmapWithFilterApplied)
                            }
                        }
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
                tvSticker.animate().scaleX(-1f).setDuration(0).start()
            }else{
                imvFlip.tintColorRes(R.color.gray)
                tvFlip.textColorRes(R.color.gray)
                imvSticker.animate().scaleX(1f).setDuration(0).start()
                tvSticker.animate().scaleX(1f).setDuration(0).start()
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

            imvLineColor.tintColorRes(R.color.gray)
            tvLineColor.textColorRes(R.color.gray)

            menuPicture.gone()
            menuOpacity.gone()
            menuLineColor.gone()
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
                .setTargetRotation(ROTATION_0)
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
//                registerCameraFlashStatus()
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 322){
            if(PermissionUtils.cameraGrant(this@DrawingActivity)){
                startCamera()
            }
        }
    }
}