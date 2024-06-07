package com.msc.ar_drawing.component.favourite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.ar_drawing.R
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.component.details.DetailsSoundAdapter
import com.msc.ar_drawing.component.details.DetailsViewModel
import com.msc.ar_drawing.databinding.ActivityFavouriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouriteActivity : BaseActivity<ActivityFavouriteBinding>() {

    private val viewModel : FavouriteViewModel by viewModels()
    private val detailsViewModel : DetailsViewModel by viewModels()

    private val detailsSoundAdapter = DetailsSoundAdapter()

    companion object{
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, FavouriteActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityFavouriteBinding {
        return ActivityFavouriteBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        viewBinding.run {
            toolbar.imvBack.setOnClickListener {
                finish()
            }
            toolbar.tvTitle.setText(R.string.txt_favourite)
        }

        buildReFavourite()
        viewModel.getListFavourite()
    }

    private fun buildReFavourite() {
        viewBinding.reFavourite.run {
            layoutManager = LinearLayoutManager(this@FavouriteActivity, RecyclerView.VERTICAL, false)
            adapter = detailsSoundAdapter
            detailsSoundAdapter.activity = this@FavouriteActivity
            detailsSoundAdapter.run {
                favouriteAction= {
                    if(it.isPlaying){
                        detailsViewModel.stopMedia()
                    }
                    viewModel.unFavourite(it)
                    removeItemUI(it)
                }
                playAction = {
                    detailsViewModel.playAudio(it)
                }

                stopAction = {
                    detailsViewModel.stopMedia()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeItemUI(it: DetailsSound) {
        CoroutineScope(Dispatchers.IO).launch {
            detailsSoundAdapter.getListData().remove(it)
            runOnUiThread {
                detailsSoundAdapter.notifyDataSetChanged()
                checkNoDataUI()
            }
        }
    }

    override fun initObserver() {
        viewModel.listFavouriteLive.observe(this){
            runOnUiThread {
                for (sound in it){
                    sound.isPlaying = false
                }
                detailsSoundAdapter.setData(it)
                checkNoDataUI()
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

    private fun checkNoDataUI() {
        if(detailsSoundAdapter.getListData().isEmpty()){
            viewBinding.llNoData.visibility = View.VISIBLE
            viewBinding.reFavourite.visibility = View.INVISIBLE
        }else{
            viewBinding.reFavourite.visibility = View.VISIBLE
            viewBinding.llNoData.visibility = View.INVISIBLE
        }
    }

    override fun onPause() {
        val index = detailsSoundAdapter.getListData().indexOfFirst { it.isPlaying }
        if(index >= 0){
            detailsSoundAdapter.getListData()[index].isPlaying = false
            detailsSoundAdapter.notifyItemChanged(index)
        }
        detailsViewModel.stopMedia()
        super.onPause()
    }
}