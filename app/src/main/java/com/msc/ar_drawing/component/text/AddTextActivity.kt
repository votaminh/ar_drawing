package com.msc.ar_drawing.component.text

import android.app.Activity
import android.content.Intent
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityAddTextBinding
import com.msc.ar_drawing.utils.DialogEx.showDialogApplyText

class AddTextActivity : BaseActivity<ActivityAddTextBinding>() {

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, AddTextActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityAddTextBinding {
        return ActivityAddTextBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.run {
             imvBack.setOnClickListener {
                 finish()
             }

            imvNext.setOnClickListener {
                showDialogApplyText()
            }
        }
    }
}