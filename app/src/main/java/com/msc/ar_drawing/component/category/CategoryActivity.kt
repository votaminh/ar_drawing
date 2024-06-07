package com.msc.ar_drawing.component.category

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.ar_drawing.BuildConfig
import com.msc.ar_drawing.admob.BaseAdmob
import com.msc.ar_drawing.admob.InterAdmob
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.details.DetailsCategoryActivity
import com.msc.ar_drawing.databinding.ActivityCategoryBinding
import com.msc.ar_drawing.utils.NetworkUtil
import com.msc.ar_drawing.utils.SpManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryActivity : BaseActivity<ActivityCategoryBinding>() {

    private val viewModel : CategoryViewModel by viewModels()
    private val categoryAdapter = CategoryAdapter()

    @Inject
    lateinit var spManager: SpManager

    var interAdmob : InterAdmob? = null

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, CategoryActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityCategoryBinding {
        return ActivityCategoryBinding.inflate(layoutInflater)
    }

    override fun initViews() {

        viewBinding.toolbar.imvBack.setOnClickListener {
            finish()
        }
        buildReCategory()

        viewModel.getAllCategory()

        loadInter()
    }

    private fun loadInter() {
        if(spManager.getBoolean(NameRemoteAdmob.INTER_CATEGORY, true) && NetworkUtil.isOnline){
            interAdmob = InterAdmob(this@CategoryActivity, BuildConfig.inter_category)
            interAdmob?.load(null)
        }
    }

    private fun buildReCategory() {
        viewBinding.reCategory.run {
            layoutManager = LinearLayoutManager(this@CategoryActivity, RecyclerView.VERTICAL, false)
            adapter = categoryAdapter
            categoryAdapter.onClick = {
                if(interAdmob != null && spManager.getBoolean(NameRemoteAdmob.INTER_CATEGORY, true)){
                    interAdmob?.showInterstitial(this@CategoryActivity, object : BaseAdmob.OnAdmobShowListener{
                        override fun onShow() {
                            DetailsCategoryActivity.start(this@CategoryActivity, it.nameRes, it.detailsRes)
                            interAdmob?.load(null)
                        }

                        override fun onError(e: String?) {
                            DetailsCategoryActivity.start(this@CategoryActivity, it.nameRes, it.detailsRes)
                            interAdmob?.load(null)
                        }
                    })
                }else{
                    DetailsCategoryActivity.start(this@CategoryActivity, it.nameRes, it.detailsRes)
                }
            }
        }
    }

    override fun initObserver() {
        viewModel.run {
            categoryListLive.observe(this@CategoryActivity){
                categoryAdapter.setData(it)
            }
        }
    }
}