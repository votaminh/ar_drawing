package com.msc.ar_drawing.component.ump

import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.splash.SplashActivity
import com.msc.ar_drawing.App
import com.msc.ar_drawing.databinding.ActivityUmpBinding
import com.msc.ar_drawing.utils.RemoteConfig
import com.msc.ar_drawing.utils.SpManager
import com.msc.ar_drawing.utils.UMPUtils


class UMPActivity : BaseActivity<ActivityUmpBinding>() {
    private val TAG = "ump_activity"

    override fun provideViewBinding(): ActivityUmpBinding = ActivityUmpBinding.inflate(layoutInflater)

    override fun initData() {
        super.initData()

        if(SpManager.getInstance(this).isUMPShowed()){
            RemoteConfig.instance().fetch()
            openSplash();
        }else{
            RemoteConfig.instance().fetch{
                initUmp()
            }
        }
    }

    private fun openSplash() {

        val app : App = application as App
        app.initAds()

        SpManager.getInstance(this).setUMPShowed(true)
        SplashActivity.start(this);
        finish()
    }

    private fun initUmp() {
        UMPUtils.init(this@UMPActivity, nextAction = {
            openSplash()
        }, true, false)
    }
}