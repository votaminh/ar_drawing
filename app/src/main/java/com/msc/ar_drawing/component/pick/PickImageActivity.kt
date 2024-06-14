package com.msc.ar_drawing.component.pick

import android.app.Activity
import android.content.Intent
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityPickImageBinding

class PickImageActivity : BaseActivity<ActivityPickImageBinding>() {

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, PickImageActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityPickImageBinding {
        return ActivityPickImageBinding.inflate(layoutInflater)
    }
}