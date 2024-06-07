package com.msc.ar_drawing.component.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.ar_drawing.BuildConfig
import com.msc.ar_drawing.admob.BannerAdmob
import com.msc.ar_drawing.admob.BaseAdmob
import com.msc.ar_drawing.admob.CollapsiblePositionType
import com.msc.ar_drawing.admob.InterAdmob
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.category.CategoryActivity
import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.component.details.DetailsSoundAdapter
import com.msc.ar_drawing.component.details.DetailsViewModel
import com.msc.ar_drawing.component.favourite.FavouriteActivity
import com.msc.ar_drawing.component.setting.SettingActivity
import com.msc.ar_drawing.databinding.ActivityMainBinding
import com.msc.ar_drawing.utils.AppEx.pickContactResult
import com.msc.ar_drawing.utils.AppEx.setContactRingtone
import com.msc.ar_drawing.utils.DialogEx.showDialogExit
import com.msc.ar_drawing.utils.NativeAdmobUtils
import com.msc.ar_drawing.utils.NetworkUtil
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel : HomeViewModel by viewModels()
    private val detailsViewModel : DetailsViewModel by viewModels()

    private val detailsSoundAdapter = DetailsSoundAdapter()

    @Inject
    lateinit var spManager : SpManager

    private var interAdmob : InterAdmob? = null
    private var selectSound : DetailsSound? = null
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

    private fun checkShowInter(nextAction : (() -> Unit)? = null) {
        if(interAdmob != null && spManager.getBoolean(NameRemoteAdmob.INTER_HOME, true)){
            interAdmob?.showInterstitial(this@HomeActivity, object : BaseAdmob.OnAdmobShowListener{
                override fun onShow() {
                    nextAction?.invoke()
                    interAdmob?.load(null)
                }

                override fun onError(e: String?) {
                    nextAction?.invoke()
                    interAdmob?.load(null)
                }

            })
        }else{
            nextAction?.invoke()
        }
    }

    override fun initViews() {
        viewBinding.run {
            llCategories.setOnClickListener {
                checkShowInter{
                    CategoryActivity.start(this@HomeActivity)
                }
            }
            llFavourite.setOnClickListener {
                checkShowInter{
                    FavouriteActivity.start(this@HomeActivity)
                }
            }
            llSetting.setOnClickListener {
                checkShowInter {
                    SettingActivity.start(this@HomeActivity)
                }
            }
        }

        spManager.saveOnBoarding()

        showBanner()
        loadInter()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkExit()
            }
        })

        NativeAdmobUtils.loadNativeExit()

        if (!checkPermission()) {
            requestPermission()
        } else {
            afterPermission()
        }
    }

    private fun afterPermission() {
//        Downloader(this@HomeActivity) {}.execute()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) checkSelfPermission(
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
            Manifest.permission.READ_MEDIA_VIDEO
        ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED else checkSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) !ActivityCompat.shouldShowRequestPermissionRationale(
                this@HomeActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) && !ActivityCompat.shouldShowRequestPermissionRationale(
                this@HomeActivity,
                Manifest.permission.READ_MEDIA_AUDIO
            ) else !ActivityCompat.shouldShowRequestPermissionRationale(
                this@HomeActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            ActivityCompat.requestPermissions(
                this@HomeActivity,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) arrayOf<String>(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) else arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                100
            )
        }
    }

    private fun checkExit() {
        if(spManager.getBoolean(NameRemoteAdmob.NATIVE_EXIT, true)){
            showDialogExit(this@HomeActivity){
                finish()
            }
        }else{
            finish()
        }
    }

    private fun loadInter() {
        if(spManager.getBoolean(NameRemoteAdmob.INTER_HOME, true) && NetworkUtil.isOnline){
            interAdmob = InterAdmob(this@HomeActivity, BuildConfig.inter_home)
            interAdmob?.load(null)
        }
    }

    private fun showBanner() {
        if(spManager.getBoolean(NameRemoteAdmob.BANNER_COLAPSE, true) && NetworkUtil.isOnline){
            val bannerAdmob = BannerAdmob(this@HomeActivity, CollapsiblePositionType.NONE)
            bannerAdmob.showBanner(
                this@HomeActivity,
                BuildConfig.banner_collap,
                viewBinding.banner
            )
        }else{
            viewBinding.banner.visibility = View.GONE
        }
    }

    override fun initData() {
        buildReBest()
        viewModel.getBestSound()
    }

    private fun buildReBest() {
        viewBinding.reDetails.run {
            val linearLayoutManager = LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false);
            layoutManager = linearLayoutManager
            adapter = detailsSoundAdapter
            detailsSoundAdapter.activity = this@HomeActivity
            detailsSoundAdapter.run {
                playAction = {
                    detailsViewModel.playAudio(it)
                }

                stopAction = {
                    detailsViewModel.stopMedia()
                }

                favouriteAction = {
                    if(it.isFavourite){
                        detailsViewModel.unfavoured(it)
                    }else{
                        detailsViewModel.favourite(it)
                    }
                }

                contactAction = {
                    if(PermissionUtils.writeSettingGrant(this@HomeActivity)){
                        if(PermissionUtils.readContactGrant(this@HomeActivity)){
                            selectSound = it
                            pickContactResult(REQUEST_PICKER_CONTACT)
                        }else{
                            PermissionUtils.requestReadContactPermission(this@HomeActivity, 322)
                        }
                    }else{
                        PermissionUtils.requestWriteSetting(this@HomeActivity, 321)
                    }
                }
            }

            setOnScrollChangeListener(object : View.OnScrollChangeListener{
                override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
//                    val latestItemIndex = linearLayoutManager.findLastVisibleItemPosition()
//                    if(itemCountInScreen == -1){
//                        itemCountInScreen = (latestItemIndex + 1)
//                        addItemAds()
//                    }
                }

            })
        }
    }

    private fun addItemAds() {
        val countItem = detailsSoundAdapter.itemCount
        var currentIndexAds = 1
        for(i in 0 until countItem){
            if(i == currentIndexAds){
                detailsSoundAdapter.getListData().add(i, DetailsSound("", 0, "", DetailsSound.ADS_FLAG))
                currentIndexAds += itemCountInScreen
            }
        }

        detailsSoundAdapter.notifyDataSetChanged()
    }

    override fun initObserver() {
        viewModel.detailsListLive.observe(this){
            runOnUiThread {
                detailsSoundAdapter.setData(it)
            }
        }

        detailsViewModel.playDoneLive.observe(this){
            val index = detailsSoundAdapter.getListData().indexOf(it)
            if(index >= 0){
                detailsSoundAdapter.getListData()[index].isPlaying = false
                detailsSoundAdapter.notifyItemChanged(index)
            }
        }
    }

    override fun onBackPressed() {
        checkExit()
    }

    override fun onPause() {
        stopItemPlayingUI()
        detailsViewModel.stopMedia()
        super.onPause()
    }

    private fun stopItemPlayingUI() {
        val index = detailsSoundAdapter.getListData().indexOfFirst { it.isPlaying }
        if(index >= 0){
            detailsSoundAdapter.getListData()[index].isPlaying = false
            detailsSoundAdapter.notifyItemChanged(index)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reCheckStateFavourite()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_PICKER_CONTACT){
            if(resultCode == RESULT_OK){
                val uri: Uri? = data?.data
                selectSound?.soundRes?.let {
                    if (uri != null) {
                        setContactRingtone(uri, it)
                    }
                }
            }
        }
    }
}