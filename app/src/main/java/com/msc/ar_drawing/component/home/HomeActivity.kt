package com.msc.ar_drawing.component.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.component.pick.PickImageActivity
import com.msc.ar_drawing.component.preview.PreviewActivity
import com.msc.ar_drawing.component.text.AddTextActivity
import com.msc.ar_drawing.databinding.ActivityHomeBinding
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val viewModel : HomeViewModel by viewModels()
    private val imageDrawAdapter = ImageDrawAdapter()

    @Inject
    lateinit var spManager : SpManager


    companion object {
        const val REQUEST_PICKER_CONTACT = 211
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, HomeActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.run {
            sketch.setOnClickListener {
                DataStatic.selectDrawMode = DrawingActivity.SKETCH_DRAWING_MODE
                PickImageActivity.start(this@HomeActivity)
            }

            trace.setOnClickListener {
                DataStatic.selectDrawMode = DrawingActivity.TRACE_DRAWING_MODE
                PickImageActivity.start(this@HomeActivity)
            }

            text.setOnClickListener {
                AddTextActivity.start(this@HomeActivity)
            }

            showAll.setOnClickListener{
                DataStatic.selectDrawMode = DrawingActivity.SKETCH_DRAWING_MODE
                PickImageActivity.start(this@HomeActivity)
            }
        }

        buildReImage()
        viewModel.getListImage()
    }

    private fun buildReImage() {
        viewBinding.reImage.run {
            layoutManager = GridLayoutManager(this@HomeActivity, 3, RecyclerView.VERTICAL, false)
            adapter = imageDrawAdapter
            imageDrawAdapter.onClick = {
                Glide.with(this@HomeActivity)
                    .asBitmap()
                    .load(Uri.parse("file:///android_asset/$it"))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            DataStatic.selectBitmap = resource
                            PreviewActivity.start(this@HomeActivity)
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
            imageDrawAdapter.setData(it.shuffled().subList(0, 9))
        }
    }
}