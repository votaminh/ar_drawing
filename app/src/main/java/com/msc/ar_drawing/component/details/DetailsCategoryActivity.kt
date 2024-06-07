package com.msc.ar_drawing.component.details

import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.databinding.ActivityDetailsCategoryBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsCategoryActivity : BaseActivity<ActivityDetailsCategoryBinding>() {

    private var detailsRes: Int? = null
    private var categoryNameRes : Int? = null

    private val viewModel : DetailsViewModel by viewModels()

    private val detailsSouAdapter = DetailsSoundAdapter()

    companion object {
        const val KEY_LIST_ITEM_RES = "KEY_LIST_ITEM_RES"
        const val KEY_NAME_CATEGORY_RES = "KEY_NAME_CATEGORY_RES"

        fun start(activity : Activity, categoryName : Int, detailsRes : Int){
            val intent = Intent(activity, DetailsCategoryActivity::class.java)
            intent.putExtra(KEY_LIST_ITEM_RES, detailsRes)
            intent.putExtra(KEY_NAME_CATEGORY_RES, categoryName)
            activity.startActivity(intent)
        }
    }

    override fun provideViewBinding(): ActivityDetailsCategoryBinding {
        return ActivityDetailsCategoryBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()
        getData();

        viewBinding.toolbar.imvBack.setOnClickListener {
            finish()
        }

        buildReDetails()

        detailsRes?.let { viewModel.getListDetailsFromRes(it) }
        categoryNameRes?.let { viewBinding.toolbar.tvTitle.setText(it) }

    }

    private fun buildReDetails() {
        viewBinding.reDetails.run {
            layoutManager = LinearLayoutManager(this@DetailsCategoryActivity, RecyclerView.VERTICAL, false)
            adapter = detailsSouAdapter
            detailsSouAdapter.activity = this@DetailsCategoryActivity
            detailsSouAdapter.run {
                playAction = {
                    viewModel.playAudio(it)
                }

                stopAction = {
                    viewModel.stopMedia()
                }

                favouriteAction = {
                    if(it.isFavourite){
                        viewModel.unfavoured(it)
                    }else{
                        viewModel.favourite(it)
                    }
                }
            }
        }
    }

    override fun initObserver() {
        viewModel.detailsListLive.observe(this){
            detailsSouAdapter.setData(it)
        }

//        viewModel.changeFavouriteLive.observe(this){
//            CoroutineScope(Dispatchers.IO).launch {
//                val index = detailsSouAdapter.getListData().indexOf(it)
//                runOnUiThread {
//                    detailsSouAdapter.notifyItemChanged(index)
//                }
//            }
//        }
        viewModel.playDoneLive.observe(this){
            val index = detailsSouAdapter.getListData().indexOf(it)
            if(index >= 0){
                detailsSouAdapter.getListData()[index].isPlaying = false
                detailsSouAdapter.notifyItemChanged(index)
            }
        }
    }

    private fun getData() {
        detailsRes = intent.getIntExtra(KEY_LIST_ITEM_RES, R.array.all_animals)
        categoryNameRes = intent.getIntExtra(KEY_NAME_CATEGORY_RES, R.string.txt_all_animal)
    }


    override fun onDestroy() {
        viewModel.stopMedia()
        super.onDestroy()
    }

    override fun onPause() {
        val index = detailsSouAdapter.getListData().indexOfFirst { it.isPlaying }
        if(index >= 0){
            detailsSouAdapter.getListData()[index].isPlaying = false
            detailsSouAdapter.notifyItemChanged(index)
        }
        viewModel.stopMedia()
        super.onPause()
    }
}