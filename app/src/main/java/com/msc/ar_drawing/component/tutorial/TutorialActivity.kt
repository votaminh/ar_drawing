package com.msc.ar_drawing.component.tutorial

import android.app.Activity
import android.content.Intent
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityTutorialBinding
import com.msc.ar_drawing.utils.ViewEx.invisible

class TutorialActivity : BaseActivity<ActivityTutorialBinding>() {

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, TutorialActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityTutorialBinding {
        return ActivityTutorialBinding.inflate(layoutInflater)
    }


    override fun initViews() {
        super.initViews()

        viewBinding.toolbar.run {
            imvLeft.setOnClickListener {
                finish()
            }

            tvTitle.setText(R.string.txt_tutorial)

            imvRight.invisible()
        }
    }
}