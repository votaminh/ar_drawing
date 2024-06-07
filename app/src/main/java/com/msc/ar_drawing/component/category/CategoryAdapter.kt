package com.msc.ar_drawing.component.category

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.flash.light.base.adapter.BaseAdapter
import com.msc.ar_drawing.databinding.ItemCategoryBinding
import com.msc.ar_drawing.domain.layer.CategoryModel

class CategoryAdapter : BaseAdapter<CategoryModel, ItemCategoryBinding>() {
    override fun provideViewBinding(parent: ViewGroup): ItemCategoryBinding {
        return ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun binData(viewBinding: ItemCategoryBinding, item: CategoryModel) {
        super.binData(viewBinding, item)

        viewBinding.run {
            tvName.setText(item.nameRes)
            Glide.with(root.context).load(Uri.parse(item.iconPath)).into(imv)
            root.setOnClickListener {
                onClick?.invoke(item)
            }
        }
    }
}