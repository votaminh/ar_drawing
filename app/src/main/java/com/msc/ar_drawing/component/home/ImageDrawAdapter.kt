package com.msc.ar_drawing.component.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.flash.light.base.adapter.BaseAdapter
import com.msc.ar_drawing.databinding.ItemImageDrawBinding

class ImageDrawAdapter : BaseAdapter<String, ItemImageDrawBinding>() {
    override fun provideViewBinding(parent: ViewGroup): ItemImageDrawBinding {
        return ItemImageDrawBinding.inflate(LayoutInflater.from(parent.context))
    }

    override fun binData(viewBinding: ItemImageDrawBinding, item: String, i: Int) {
        super.binData(viewBinding, item, i)

        viewBinding.run {
            Glide.with(root.context)
                .load(Uri.parse("file:///android_asset/$item"))
                .into(imv)

            root.setOnClickListener{
                onClick?.invoke(item)
            }
        }
    }
}