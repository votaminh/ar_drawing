package com.msc.ar_drawing.component.preview

import android.app.Activity
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.msc.ar_drawing.BuildConfig
import com.msc.ar_drawing.admob.BannerAdmob
import com.msc.ar_drawing.admob.CollapsiblePositionType
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.databinding.ActivityPreviewBinding
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.SpManager

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

        showBanner()
    }

    private fun showBanner() {
        if(SpManager.getInstance(this@PreviewActivity).getBoolean(NameRemoteAdmob.BANNER_COLLAPSE_PREVIEW, true)){
            val bannerAdmob = BannerAdmob(this, CollapsiblePositionType.BOTTOM)
            bannerAdmob.showBanner(this@PreviewActivity, BuildConfig.banner_collapse_preview, viewBinding.banner)
        }else{
            viewBinding.banner.visibility = View.GONE
        }
    }
}