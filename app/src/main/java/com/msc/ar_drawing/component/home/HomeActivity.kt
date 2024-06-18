package com.msc.ar_drawing.component.home

import android.app.Activity
import android.content.Intent
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.component.pick.PickImageActivity
import com.msc.ar_drawing.component.text.AddTextActivity
import com.msc.ar_drawing.databinding.ActivityHomeBinding
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
        }
    }
}