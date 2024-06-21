package com.msc.ar_drawing.component.permission

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.home.HomeActivity
import com.msc.ar_drawing.databinding.ActivityPermissonBinding
import com.msc.ar_drawing.utils.NativeAdmobUtils
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissonBinding>() {

    @Inject
    lateinit var spManager: SpManager

    private val stateWriteSetting = MutableLiveData(false)

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, PermissionActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityPermissonBinding {
        return ActivityPermissonBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.run {
            llWriteSetting.setOnClickListener {
                PermissionUtils.requestCamera(this@PermissionActivity, 342)
            }

            ivDone.setOnClickListener {
                HomeActivity.start(this@PermissionActivity)
                finish()
            }
        }

        checkState()
    }

    private fun checkState() {
        stateWriteSetting.postValue(PermissionUtils.cameraGrant(this@PermissionActivity))
    }

    override fun initObserver() {
        super.initObserver()

        stateWriteSetting.observe(this){
            viewBinding.sw.isChecked = it
            checkShowNextBtn()
        }

        if(!spManager.getBoolean(NameRemoteAdmob.NATIVE_PERMISSION, true)){
            viewBinding.flAdplaceholder.visibility = View.GONE
        }

        NativeAdmobUtils.permissionNativeAdmob?.run {
            nativeAdLive.observe(this@PermissionActivity){
                if(available() && spManager.getBoolean(NameRemoteAdmob.NATIVE_PERMISSION, true)){
                    showNative(viewBinding.flAdplaceholder, null)
                }else{
                    viewBinding.flAdplaceholder.visibility = View.GONE
                }
            }
        }
    }

    private fun checkShowNextBtn() {
        if(stateWriteSetting.value == true){
            viewBinding.ivDone.visibility = View.VISIBLE
        }else{
            viewBinding.ivDone.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        NativeAdmobUtils.permissionNativeAdmob?.reLoad()
        super.onResume()
        checkState()
    }
}