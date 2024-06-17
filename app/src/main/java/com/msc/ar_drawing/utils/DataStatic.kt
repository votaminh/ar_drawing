package com.msc.ar_drawing.utils

import android.graphics.Bitmap
import com.msc.ar_drawing.domain.layer.TypeDraw

class DataStatic {
    companion object {
        var selectBitmap : Bitmap? = null

        var selectTypeDraw : TypeDraw = TypeDraw.SKETCH
    }
}