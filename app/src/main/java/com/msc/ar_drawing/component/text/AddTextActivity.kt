package com.msc.ar_drawing.component.text

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.drawing.DrawingActivity
import com.msc.ar_drawing.databinding.ActivityAddTextBinding
import com.msc.ar_drawing.utils.DataStatic
import com.msc.ar_drawing.utils.DialogEx.showDialogApplyText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTextActivity : BaseActivity<ActivityAddTextBinding>() {

    private val fontAdapter = FontAdapter()
    private val colorAdapter = ColorAdapter()
    private val viewModel : AddTextViewModel by viewModels()

    companion object {
        fun start(activity : Activity){
            activity.startActivity(Intent(activity, AddTextActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityAddTextBinding {
        return ActivityAddTextBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        super.initViews()

        viewBinding.run {
             imvBack.setOnClickListener {
                 finish()
             }

            imvNext.setOnClickListener {
                showDialogApplyText(
                    sketchAction = {
                        DataStatic.selectDrawMode = DrawingActivity.SKETCH_DRAWING_MODE
                        openDraw()
                    },
                    traceAction = {
                        DataStatic.selectDrawMode = DrawingActivity.TRACE_DRAWING_MODE
                        openDraw()
                    })
            }
        }

        buildReFont()
        viewModel.getFonts()

        buildReColor()
        viewModel.getColors()
    }

    private fun openDraw() {
        var color = Color.BLACK
        colorAdapter.select.let {
            if(it != -1){
                color = colorAdapter.getListData()[it]
            }
        }

        var typeface : Typeface? = null
        fontAdapter.select.let {
            if(it != -1){
                typeface = fontAdapter.getListData()[it]
            }
        }

        DrawingActivity.startWithText(
            this@AddTextActivity,
            DataStatic.selectDrawMode,
            viewBinding.edInput.text.toString().trim(),
            color,
            typeface
        )
    }

    private fun buildReColor() {
        viewBinding.reColor.run {
            layoutManager = LinearLayoutManager(this@AddTextActivity, RecyclerView.HORIZONTAL, false)
            adapter = colorAdapter
            colorAdapter.onClick = {
                viewBinding.edInput.setTextColor(it)
            }
        }
    }

    override fun initObserver() {
        super.initObserver()

        viewModel.run {
            fontsLive.observe(this@AddTextActivity){
                fontAdapter.setData(it)
            }

            colorsLive.observe(this@AddTextActivity){
                colorAdapter.setData(it)
            }
        }
    }

    private fun buildReFont() {
        viewBinding.reFont.run {
            layoutManager = LinearLayoutManager(this@AddTextActivity, RecyclerView.HORIZONTAL, false)
            adapter = fontAdapter
            fontAdapter.onClick = {
                viewBinding.edInput.typeface = it
                val index = fontAdapter.getListData().indexOf(it)
                fontAdapter.setSelectItem(index)
            }
        }
    }
}