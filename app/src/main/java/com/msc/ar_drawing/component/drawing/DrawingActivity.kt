package com.msc.ar_drawing.component.drawing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import com.msc.ar_drawing.utils.ViewEx.textColorRes
import com.msc.ar_drawing.utils.ViewEx.tintColorRes
import com.msc.ar_drawing.utils.ViewEx.visible
import java.util.concurrent.Executors

class DrawingActivity : BaseActivity<ActivityMain1Binding>() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var imageAnalyzer: ImageAnalysis? = null

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, DrawingActivity::class.java))
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
                showToast("flip")
            }

            Glide.with(this@DrawingActivity).load(DataStatic.selectBitmap).into(imvSticker)

            sliderOpacity.addOnChangeListener(object : Slider.OnChangeListener{
                @SuppressLint("RestrictedApi")
                override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                    onChangeOpacity(value);
                }
            })
        }
    }

    private fun onChangeOpacity(value: Float) {
        viewBinding.run {
            imvSticker.animate().alpha(value).setDuration(0).start()
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