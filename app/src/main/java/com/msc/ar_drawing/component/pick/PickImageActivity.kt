package com.msc.ar_drawing.component.pick

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.component.home.ImageDrawAdapter
import com.msc.ar_drawing.component.preview.PreviewActivity
import com.msc.ar_drawing.component.tutorial.TutorialActivity
import com.msc.ar_drawing.databinding.ActivityPickImageBinding
import com.msc.ar_drawing.databinding.LayoutToolbarBinding
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PickImageActivity : BaseActivity<ActivityPickImageBinding>() {

    val viewModel : PickImageViewModel by viewModels()
    private val imageDrawAdapter = ImageDrawAdapter()

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

        buildReImage()
        viewModel.getListImage()
    }

    private fun buildReImage() {
        viewBinding.reImage.run {
            layoutManager = GridLayoutManager(this@PickImageActivity, 3, RecyclerView.VERTICAL, false)
            adapter = imageDrawAdapter
            imageDrawAdapter.onClick = {
                Glide.with(this@PickImageActivity)
                    .asBitmap()
                    .load(Uri.parse("file:///android_asset/$it"))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            DataStatic.selectBitmap = resource
                            PreviewActivity.start(this@PickImageActivity)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                        }
                    })
            }
        }
    }

    override fun initObserver() {
        super.initObserver()

        viewModel.imagesPathLive.observe(this){
            imageDrawAdapter.setData(it)
        }
    }

    private fun updateTitle(layoutToolbarBinding: LayoutToolbarBinding) {
        when(DataStatic.selectDrawMode){
            DrawingActivity.SKETCH_DRAWING_MODE -> {
                layoutToolbarBinding.tvTitle.setText(R.string.txt_sketch)
            }
            DrawingActivity.TRACE_DRAWING_MODE -> {
                layoutToolbarBinding.tvTitle.setText(R.string.txt_trace)
            }
            else ->{

            }
        }
    }

    private fun captureCamera(){
        if(PermissionUtils.cameraGrant(this@PickImageActivity)){
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }else{
            PermissionUtils.requestCamera(this@PickImageActivity, 322)
        }
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