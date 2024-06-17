package com.msc.ar_drawing.component.pick

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.preview.PreviewActivity
import com.msc.ar_drawing.component.tutorial.TutorialActivity
import com.msc.ar_drawing.databinding.ActivityPickImageBinding
import com.msc.ar_drawing.databinding.LayoutToolbarBinding
import com.msc.ar_drawing.domain.layer.TypeDraw
import com.msc.ar_drawing.utils.DataStatic


class PickImageActivity : BaseActivity<ActivityPickImageBinding>() {

    companion object {

        const val REQUEST_PICK_GALLERY = 112
        const val REQUEST_CAMERA = 113

        fun start(activity : Activity){
            activity.startActivity(Intent(activity, PickImageActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityPickImageBinding {
        return ActivityPickImageBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        viewBinding.run {
            gallery.setOnClickListener {
                openGallery()
            }

            camera.setOnClickListener {
                captureCamera()
            }

            toolbar.run {
                imvRight.setOnClickListener {
                    TutorialActivity.start(this@PickImageActivity)
                }

                imvLeft.setOnClickListener {
                    finish()
                }

                updateTitle(this)
            }
        }
    }

    private fun updateTitle(layoutToolbarBinding: LayoutToolbarBinding) {
        when(DataStatic.selectTypeDraw){
            TypeDraw.SKETCH -> {
                layoutToolbarBinding.tvTitle.setText(R.string.txt_sketch)
            }
            TypeDraw.TRACE -> {
                layoutToolbarBinding.tvTitle.setText(R.string.txt_trace)
            }
            else ->{

            }
        }
    }

    private fun captureCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        try {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_GALLERY)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_PICK_GALLERY ->{
                    val stream = data?.data?.let {
                        contentResolver.openInputStream(it)
                    }
                    DataStatic.selectBitmap = BitmapFactory.decodeStream(stream, null, BitmapFactory.Options())
                }

                REQUEST_CAMERA -> {
                    if (data != null) {
                        DataStatic.selectBitmap = data.extras?.get("data") as Bitmap?
                    }
                }
            }

            DataStatic.selectBitmap?.let {
                openPreview()
            }
        }
    }

    private fun openPreview() {
        PreviewActivity.start(this@PickImageActivity)
    }
}