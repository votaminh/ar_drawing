package com.msc.ar_drawing.component.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.language.LanguageActivity
import com.msc.ar_drawing.databinding.ActivitySettingBinding
import com.msc.ar_drawing.utils.AppEx.shareText

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    companion object {
        fun start(activity: Activity){
            activity.startActivity(Intent(activity, SettingActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.run {
            imvBack.setOnClickListener {
                finish()
            }

            language.setOnClickListener {
                LanguageActivity.start(this@SettingActivity, false)
            }

            share.setOnClickListener {
                shareText("https://play.google.com/store/apps/details?id=$packageName")
            }

            rate.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }

            privacy.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")))
            }
        }

    }
}