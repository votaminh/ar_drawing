package com.msc.ar_drawing.component.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.language.LanguageActivity
import com.msc.ar_drawing.databinding.ActivitySettingBinding
import com.msc.ar_drawing.utils.AppEx.revertRingtone
import com.msc.ar_drawing.utils.AppEx.shareText
import com.msc.ar_drawing.utils.DialogEx.showDialogSuccess
import com.msc.ar_drawing.utils.PermissionUtils

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, SettingActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        viewBinding.toolbar.run {
            imvBack.setOnClickListener {
                finish()
            }
            tvTitle.setText(R.string.txt_setting)
        }

        viewBinding.run {
            rate5Star.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
            share.setOnClickListener {
                shareText("https://play.google.com/store/apps/details?id=$packageName")
            }
            privacy.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacypolicyanimalringtone/trang-ch%E1%BB%A7")))
            }
            language.setOnClickListener {
                LanguageActivity.start(this@SettingActivity, false)
            }
            revertRingtone.setOnClickListener {
                if(PermissionUtils.writeSettingGrant(this@SettingActivity)){
                    revertRingtone()
                    showDialogSuccess(getString(R.string.txt_revert_to_default_success))
                }else{
                    showDialogSuccess(getString(R.string.txt_you_not_set_ringtone_yet))
                }
            }
        }
    }
}