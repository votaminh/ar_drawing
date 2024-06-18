package com.msc.ar_drawing.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.msc.ar_drawing.component.drawing.DrawingActivity

class DataStatic {
    companion object {
        var selectBitmap : Bitmap? = null
        var selectDrawMode : Int = DrawingActivity.SKETCH_DRAWING_MODE
        var currentColorText: Int = Color.BLACK
    }
}