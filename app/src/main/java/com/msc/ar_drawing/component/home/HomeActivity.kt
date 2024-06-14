package com.msc.ar_drawing.comp

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import com.msc.ar_drawing.admob.InterAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityMainBinding
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityMainBinding>() {


    @Inject
    lateinit var spManager : SpManager

    private var interAdmob : InterAdmob? = null
    private var itemCountInScreen =  -1

    companion object {
        const val REQUEST_PICKER_CONTACT = 211
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, HomeActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}