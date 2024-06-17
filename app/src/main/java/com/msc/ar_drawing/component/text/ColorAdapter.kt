package com.msc.ar_drawing.component.text

import android.view.LayoutInflater
import android.view.ViewGroup
import com.flash.light.base.adapter.BaseAdapter
import com.msc.ar_drawing.databinding.ItemColorBinding
import com.msc.ar_drawing.utils.ViewEx.gone
import com.msc.ar_drawing.utils.ViewEx.invisible
import com.msc.ar_drawing.utils.ViewEx.visible

class ColorAdapter : BaseAdapter<Int, ItemColorBinding>() {
    override fun provideViewBinding(parent: ViewGroup): ItemColorBinding {
        return ItemColorBinding.inflate(LayoutInflater.from(parent.context))
    }

    override fun binData(viewBinding: ItemColorBinding, item: Int, i: Int) {
        super.binData(viewBinding, item, i)

        viewBinding.run {

            if(i == select){
                mask.visible()
            }else{
                mask.gone()
            }

            if(item == -1){
                itemColor.invisible()
                imvPick.visible()
            }else{
                itemColor.visible()
                imvPick.invisible()

                itemColor.setCardBackgroundColor(item)
            }

            root.setOnClickListener {
                onClick?.invoke(item)
                setSelectItem(i)
            }
        }
    }
}