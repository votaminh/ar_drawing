package com.msc.ar_drawing.component.details

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.flash.light.base.adapter.BaseAdapter
import com.msc.ar_drawing.R
import com.msc.ar_drawing.component.favourite.FavouriteActivity
import com.msc.ar_drawing.databinding.ItemSoundBinding
import com.msc.ar_drawing.utils.AppEx.setAlarmSound
import com.msc.ar_drawing.utils.AppEx.setNotificationSound
import com.msc.ar_drawing.utils.AppEx.setRingtone
import com.msc.ar_drawing.utils.DialogEx.showDialogRequestWriteSettingPermission
import com.msc.ar_drawing.utils.DialogEx.showDialogSuccess
import com.msc.ar_drawing.utils.PermissionUtils
import com.msc.ar_drawing.utils.ViewEx.tintColorRes


class DetailsSoundAdapter : BaseAdapter<DetailsSound, ItemSoundBinding>() {

    var playAction : ((DetailsSound) -> Unit)? = null
    var stopAction : ((DetailsSound) -> Unit)? = null
    var favouriteAction : ((DetailsSound) -> Unit)? = null
    var contactAction : ((DetailsSound) -> Unit)? = null

    var activity : Activity? = null

    override fun provideViewBinding(parent: ViewGroup): ItemSoundBinding {
        return ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        holder.item.llSub.visibility = View.GONE
        super.onViewRecycled(holder)
    }

    @SuppressLint("CheckResult")
    override fun binData(viewBinding: ItemSoundBinding, item: DetailsSound) {
        viewBinding.run {
            if(item.flag == DetailsSound.ADS_FLAG){
                layoutItem.visibility = View.GONE
                flAdplaceholder.visibility = View.VISIBLE

            }else{

                layoutItem.visibility = View.VISIBLE
                flAdplaceholder.visibility = View.GONE

                tvName.text = item.name

                val requestOptions = RequestOptions()
                requestOptions.run {
                    placeholder(R.color.gray)
                    error(R.drawable.ic_miss)
                }

                Glide.with(root.context).setDefaultRequestOptions(requestOptions).load(Uri.parse(item.iconAssetPath)).into(imv)

                if(item.isFavourite){
                    imvFavourite.tintColorRes(R.color.red)
                }else{
                    imvFavourite.tintColorRes(R.color.white)
                }

                if(item.isPlaying){
                    imvPlay.setImageResource(R.drawable.ic_pause)
                }else{
                    imvPlay.setImageResource(R.drawable.ic_play)
                }

                root.setOnClickListener {
                    val soundPlaying = getListData().firstOrNull { it.isPlaying }

                    soundPlaying?.let { soundPlaying ->
                        if(soundPlaying != item){
                            soundPlaying.isPlaying = false
                            notifyItemChanged(getListData().indexOf(soundPlaying))
                        }
                    }

                    if(item.isPlaying){
                        item.isPlaying = false
                        stopAction?.invoke(item)
                    }else{
                        item.isPlaying = true
                        playAction?.invoke(item)
                    }

                    if(item.isPlaying){
                        imvPlay.setImageResource(R.drawable.ic_pause)
                    }else{
                        imvPlay.setImageResource(R.drawable.ic_play)
                    }
                }

                imvFavourite.setOnClickListener {
                    favouriteAction?.invoke(item)
                    if(activity is FavouriteActivity){
                    }else {
                        item.isFavourite = !item.isFavourite
                        if(item.isFavourite){
                            imvFavourite.tintColorRes(R.color.red)
                        }else{
                            imvFavourite.tintColorRes(R.color.white)
                        }
                    }
                }
                imvSetting.setOnClickListener {
                    if(llSub.visibility == View.VISIBLE){
                        llSub.visibility = View.GONE
                    }else{
                        llSub.visibility = View.VISIBLE
                    }
                }
                ringtone.setOnClickListener {
                    activity?.run {
                        if(PermissionUtils.writeSettingGrant(this)){
                            setRingtone(item.name, item.soundRes)
                            showDialogSuccess(getString(R.string.txt_ringtone_success))
                        }else{
                            showDialogRequestWriteSettingPermission{
                                PermissionUtils.requestWriteSetting(this, 221)
                            }
                        }
                    }
                }
                contact.setOnClickListener {
                    contactAction?.invoke(item)
                }
                sms.setOnClickListener {
                    activity?.run {
                        if(PermissionUtils.writeSettingGrant(this)){
                            setNotificationSound(item.name, item.soundRes)
                            showDialogSuccess(getString(R.string.txt_ringtone_success))
                        }else{
                            showDialogRequestWriteSettingPermission{
                                PermissionUtils.requestWriteSetting(this, 221)
                            }
                        }
                    }
                }
                alarm.setOnClickListener {
                    activity?.run {
                        if(PermissionUtils.writeSettingGrant(this)){
                            setAlarmSound(item.name, item.soundRes)
                            showDialogSuccess(getString(R.string.txt_set_ringtone_alarm))
                        }else{
                            showDialogRequestWriteSettingPermission{
                                PermissionUtils.requestWriteSetting(this, 221)
                            }
                        }
                    }
                }
            }
        }
    }
}