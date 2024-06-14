package com.msc.ar_drawing.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import com.msc.ar_drawing.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


object AppEx {
    fun Activity.shareText(text : String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            this.getString(R.string.txt_share)
        )
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(
            intent,
            this.getString(R.string.txt_share)
        ))
    }

    fun Context.getDeviceLanguage(): String {
        return Locale.getDefault().language
    }

    fun Context.setAppLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}