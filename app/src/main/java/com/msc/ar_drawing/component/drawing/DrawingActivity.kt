package com.msc.ar_drawing.component.drawing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.Surface.ROTATION_0
import android.view.View
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider
import com.msc.ar_drawing.BuildConfig
import com.msc.ar_drawing.R
import com.msc.ar_drawing.admob.BannerAdmob
import com.msc.ar_drawing.admob.CollapsiblePositionType
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.text.AddTextViewModel
import com.msc.ar_drawing.component.text.ColorAdapter
import com.msc.ar_drawing.databinding.ActivityMain1Binding
import com.msc.ar_drawing.utils.AppEx.replaceWhiteToTransparentBitmap
import com.msc.ar_drawing.utils.DialogEx.showPickerColor
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.SpManager
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

    private var isLock = false
    private var assetType: Int = BITMAP_ASSET
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private val colorAdapter = ColorAdapter()

    private var isFlip = false
    private var isFlash = false
    private var scaleFactor = 1.0f

    private val viewModel : AddTextViewModel by viewModels()

    var preTouchX = 0f
    var preTouchY = 0f

    private var mScaleDetector: ScaleGestureDetector? = null

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

            imvLock.setOnClickListener {
                lockSticker()
            }

            imvBack.setOnClickListener {
                finish()
            }

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

            touchStickerEvent()
        }

        checkDataIntent()

        buildReColor()
        viewModel.getColors()

        showBanner()
    }

    private fun showBanner() {
        if(SpManager.getInstance(this@DrawingActivity).getBoolean(NameRemoteAdmob.BANNER_COLLAPSE_DRAW_TEXT, true)){
            val bannerAdmob = BannerAdmob(this, CollapsiblePositionType.BOTTOM)
            bannerAdmob.showBanner(this@DrawingActivity, BuildConfig.banner_collapse_draw_text, viewBinding.banner)
        }else{
            viewBinding.banner.visibility = View.GONE
        }
    }

    private fun lockSticker() {
        if(isLock){
            isLock = false
            viewBinding.imvLock.tintColorRes(R.color.white)
        }else{
            isLock = true
            viewBinding.imvLock.tintColorRes(R.color.app_main)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun touchStickerEvent() {

        mScaleDetector = ScaleGestureDetector(this@DrawingActivity, object : ScaleGestureDetector.OnScaleGestureListener{
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor

                // Ensure the scale factor remains within a reasonable range
                scaleFactor = maxOf(0.1f, minOf(scaleFactor, 5.0f))

                viewBinding.run {
                    // Apply the scaling factor, considering if the image is flipped
                    if(imvSticker.isVisible){
                        viewBinding.imvSticker.scaleX = if (isFlip) -scaleFactor else scaleFactor
                        viewBinding.imvSticker.scaleY = scaleFactor
                    }else {
                        viewBinding.tvSticker.scaleX = if (isFlip) -scaleFactor else scaleFactor
                        viewBinding.tvSticker.scaleY = scaleFactor
                    }
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // Optional: You can handle any cleanup or final adjustments here
            }

        })

        viewBinding.touchListener.setOnTouchListener { view, motionEvent ->

            if(isLock){
                return@setOnTouchListener false
            }

            mScaleDetector?.onTouchEvent(motionEvent)

            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> {
                    if(preTouchX == 0f){
                        preTouchX = motionEvent.x
                        preTouchY = motionEvent.y
                    }
                    viewBinding.run {
                        if(imvSticker.isVisible){
                            imvSticker.animate().translationXBy(motionEvent.x - preTouchX).setDuration(0).start()
                            imvSticker.animate().translationYBy(motionEvent.y - preTouchY).setDuration(0).start()
                        }else {
                            tvSticker.animate().translationXBy(motionEvent.x - preTouchX).setDuration(0).start()
                            tvSticker.animate().translationYBy(motionEvent.y - preTouchY).setDuration(0).start()
                        }
                    }
                    preTouchX = motionEvent.x
                    preTouchY = motionEvent.y
                }

                MotionEvent.ACTION_UP -> {
                    preTouchX = 0f
                    preTouchY = 0f
                }
            }

            return@setOnTouchListener true
        }
    }

    private fun turnFlash() {
        viewBinding.run {
            if(isFlash){
                turnOffFlash()
            }else{
                isFlash = true
                imvFlash.tintColorRes(R.color.app_main)
                tvFlash.textColorRes(R.color.app_main)
            }
        }

        val cameraControl = camera?.cameraControl
        cameraControl?.enableTorch(isFlash)
    }

    private fun turnOffFlash() {
        isFlash = false
        viewBinding.imvFlash.tintColorRes(R.color.gray)
        viewBinding.tvFlash.textColorRes(R.color.gray)
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
                        tvTitle.setText(R.string.txt_sketch)
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
                        tvTitle.setText(R.string.txt_trace)
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
            // Apply the current scale factor with the flip adjustment

            if(isFlip){
                imvFlip.tintColorRes(R.color.app_main)
                tvFlip.textColorRes(R.color.app_main)
            }else{
                imvFlip.tintColorRes(R.color.gray)
                tvFlip.textColorRes(R.color.gray)
            }

            imvSticker.scaleX = if (isFlip) -scaleFactor else scaleFactor
            tvSticker.scaleX = if (isFlip) -scaleFactor else scaleFactor

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

    override fun onPause() {
        turnOffFlash()
        super.onPause()
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