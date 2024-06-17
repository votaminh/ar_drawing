package com.msc.ar_drawing.component.home

import android.app.Activity
import android.content.Intent
import com.msc.ar_drawing.admob.InterAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.pick.PickImageActivity
import com.msc.ar_drawing.databinding.ActivityHomeBinding
import com.msc.ar_drawing.domain.layer.TypeDraw
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {


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
                DataStatic.selectTypeDraw = TypeDraw.SKETCH
                PickImageActivity.start(this@HomeActivity)
            }

            trace.setOnClickListener {
                DataStatic.selectTypeDraw = TypeDraw.TRACE
                PickImageActivity.start(this@HomeActivity)
            }
        }
    }
}