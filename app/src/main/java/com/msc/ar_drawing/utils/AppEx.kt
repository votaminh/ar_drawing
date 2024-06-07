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
import com.msc.ar_drawing.utils.i_ringtone.StorageConfiguration
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

    fun Activity.setRingtone(name : String, rawResourceId : Int){

        val defaultUri = SpManager.getInstance(this).getDefaultRingtone()
        if(defaultUri.isNullOrEmpty()){
            val defaultUri = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_RINGTONE);
            SpManager.getInstance(this).setDefaultRingtone(defaultUri.toString())
        }

        insertAndSetRingtone(name, rawResourceId, RingtoneManager.TYPE_RINGTONE)
    }

    private fun Activity.insertAndSetRingtone(name: String, rawResourceId: Int, type: Int) {
        val values = ContentValues()

        val ringtoneFile = File(StorageConfiguration.getDownloadDirectory().absolutePath, "$name.mp3")

        if(!ringtoneFile.exists()){
            val inputStream = this.resources.openRawResource(rawResourceId)
            val outputStream = FileOutputStream(ringtoneFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
        }

        values.put(MediaStore.MediaColumns.TITLE, name)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
        values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length())
        values.put(MediaStore.Audio.Media.IS_MUSIC, false)

        if(type == RingtoneManager.TYPE_RINGTONE){
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            values.put(MediaStore.Audio.Media.IS_ALARM, false)
        }else if(type == RingtoneManager.TYPE_NOTIFICATION){
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
            values.put(MediaStore.Audio.Media.IS_ALARM, false)
        }else if(type == RingtoneManager.TYPE_ALARM){
            values.put(MediaStore.Audio.Media.IS_RINGTONE, false)
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            values.put(MediaStore.Audio.Media.IS_ALARM, true)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                name
            )
            val newUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            contentResolver.openOutputStream(newUri!!).use { os ->
                val size = ringtoneFile.length().toInt()
                val bytes = ByteArray(size)
                try {
                    val buf = BufferedInputStream(FileInputStream(ringtoneFile))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()
                    os?.run {
                        write(bytes)
                        close()
                        flush()
                    }
                    refreshStorage(this, ringtoneFile.absolutePath);
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            RingtoneManager.setActualDefaultRingtoneUri(this, type, newUri)
        } else {
            values.put(MediaStore.MediaColumns.DATA, ringtoneFile.absolutePath)
            val uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.absolutePath)
            contentResolver.delete(
                uri!!,
                MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.absolutePath + "\"",
                null
            )
            val newUri: Uri? = contentResolver.insert(uri, values)
            RingtoneManager.setActualDefaultRingtoneUri(this, type, newUri)
            MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.absolutePath)?.let {
                contentResolver
                    .insert(
                        it, values
                    )
            }
        }
    }

    fun refreshStorage(mContext: Context, filePath: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mContext.sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())
                )
            )
        } else {
            MediaScannerConnection.scanFile(
                mContext, arrayOf(filePath), null
            ) { path, uri ->
            }
        }
    }

    fun Activity.setNotificationSound(name : String, soundRes : Int){
        val defaultUri = SpManager.getInstance(this).getDefaultRingtoneNotification()
        if(defaultUri.isNullOrEmpty()){
            val defaultUri = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_NOTIFICATION);
            SpManager.getInstance(this).setDefaultRingtoneNotification(defaultUri.toString())
        }

        insertAndSetRingtone(name, soundRes, RingtoneManager.TYPE_NOTIFICATION)
    }

    fun Activity.setAlarmSound(name : String, soundRes : Int){
        val defaultUri = SpManager.getInstance(this).getDefaultRingtoneAlarm()
        if(defaultUri.isNullOrEmpty()){
            val defaultUri = RingtoneManager.getActualDefaultRingtoneUri(applicationContext, RingtoneManager.TYPE_ALARM);
            SpManager.getInstance(this).setDefaultRingtoneAlarm(defaultUri.toString())
        }

        insertAndSetRingtone(name, soundRes, RingtoneManager.TYPE_ALARM)
    }

    fun Activity.setContactRingtone(contactUri : Uri, soundRes : Int){
        val uri = Uri.parse("android.resource://$packageName/$soundRes")

        val data = contactUri
        val lastPathSegment = data!!.lastPathSegment
        val query: Cursor? = contentResolver.query(
            data,
            arrayOf<String>("_id", "display_name", "has_phone_number"),
            null as String?,
            null as Array<String?>?,
            null as String?
        )
        query?.moveToFirst()
        val string = query?.getString(query.getColumnIndexOrThrow("_id"))
        val string2 = query?.getString(query.getColumnIndexOrThrow("display_name"))
        val withAppendedPath = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, string)
        query?.close()
        val contentValues = ContentValues()
        contentValues.put("raw_contact_id", lastPathSegment)
        contentValues.put("custom_ringtone", uri.toString())
        contentResolver.update(withAppendedPath, contentValues, null as String?, null as Array<String?>?)
    }

    fun Activity.revertRingtone(){
        val defaultUri = SpManager.getInstance(this).getDefaultRingtone()
        defaultUri?.let {
            if(it.isNotEmpty()){
                val uri = Uri.parse(it)
                RingtoneManager.setActualDefaultRingtoneUri(
                    this,
                    RingtoneManager.TYPE_RINGTONE,
                    uri
                );
            }
        }

        val defaultUriNotification = SpManager.getInstance(this).getDefaultRingtoneNotification()
        defaultUriNotification?.let {
            if(it.isNotEmpty()){
                val uri = Uri.parse(it)
                RingtoneManager.setActualDefaultRingtoneUri(
                    this,
                    RingtoneManager.TYPE_NOTIFICATION,
                    uri
                );
            }
        }

        val defaultUriAlarm = SpManager.getInstance(this).getDefaultRingtoneAlarm()
        defaultUriAlarm?.let {
            if(it.isNotEmpty()){
                val uri = Uri.parse(it)
                RingtoneManager.setActualDefaultRingtoneUri(
                    this,
                    RingtoneManager.TYPE_ALARM,
                    uri
                );
            }
        }
    }

    fun Activity.pickContactResult(request : Int){
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        startActivityForResult(contactPickerIntent, request)
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