package com.msc.ar_drawing.component.text

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import com.flash.light.base.adapter.BaseAdapter
import com.msc.ar_drawing.R
import com.msc.ar_drawing.databinding.ItemFontBinding
import com.msc.ar_drawing.utils.ViewEx.textColorRes

class FontAdapter : BaseAdapter<Typeface, ItemFontBinding>() {
    override fun provideViewBinding(parent: ViewGroup): ItemFontBinding {
        return ItemFontBinding.inflate(LayoutInflater.from(parent.context))
    }

    override fun binData(viewBinding: ItemFontBinding, item: Typeface, i: Int) {

        viewBinding.run {

            if(i == select){
                tvFont.setBackgroundResource(R.drawable.bg_main_round_stroke)
                tvFont.textColorRes(R.color.app_main)
            }else{
                tvFont.setBackgroundResource(R.drawable.bg_black_round_stroke)
                tvFont.textColorRes(R.color.black)
            }

            tvFont.typeface = item
            root.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}