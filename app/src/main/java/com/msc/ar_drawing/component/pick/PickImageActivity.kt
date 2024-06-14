package com.msc.ar_drawing.component.pick

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import com.msc.ar_drawing.base.activity.BaseActivity
import com.msc.ar_drawing.component.preview.PreviewActivity
import com.msc.ar_drawing.databinding.ActivityPickImageBinding
import com.msc.ar_drawing.utils.DataStatic

class PickImageActivity : BaseActivity<ActivityPickImageBinding>() {

    companion object {

        const val REQUEST_PICK_GALLERY = 112

        fun start(activity : Activity){
            activity.startActivity(Intent(activity, PickImageActivity::class.java))
        }
    }

    override fun provideViewBinding(): ActivityPickImageBinding {
        return ActivityPickImageBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        viewBinding.run {
            gallery.setOnClickListener {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = "android.intent.action.GET_CONTENT"
        try {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_GALLERY)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PICK_GALLERY){
                val stream = data?.data?.let {
                    contentResolver.openInputStream(it)
                }
                DataStatic.selectBitmap = BitmapFactory.decodeStream(stream, null, BitmapFactory.Options())
                DataStatic.selectBitmap?.let {
                    openPreview()
                }
            }
        }
    }

    private fun openPreview() {
        PreviewActivity.start(this@PickImageActivity)
    }
}