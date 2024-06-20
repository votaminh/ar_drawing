package com.msc.ar_drawing.component.preview

import android.app.Activity
import android.content.Intent
import com.bumptech.glide.Glide
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.databinding.ActivityPreviewBinding
import com.msc.ar_drawing.utils.DataStatic

class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, PreviewActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityPreviewBinding {
        return ActivityPreviewBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        viewBinding.run {
            imvNext.setOnClickListener {
                DataStatic.selectBitmap?.let { it1 ->
                    DrawingActivity.startWithBitmap(
                        this@PreviewActivity,
                        DataStatic.selectDrawMode,
                        it1
                    )
                }
            }

            imvBack.setOnClickListener{
                finish()
            }

            Glide.with(this@PreviewActivity).load(DataStatic.selectBitmap).into(imvPreview)
        }
    }
}