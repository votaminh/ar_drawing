package com.msc.ar_drawing.utils

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.msc.ar_drawing.App
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.admob.NameRemoteUINative
import org.json.JSONObject

class RemoteConfig {

    private val TAG = "remoteConfig"

    companion object {
        private var mInstance : RemoteConfig? = null

        fun instance(): RemoteConfig {
            if(mInstance == null){
                mInstance = RemoteConfig()
            }
            return mInstance as RemoteConfig
        }

    }

    fun fetch(success : (() -> Unit)? = null) {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                } else {

                }
                success?.invoke()
                updateConfig()
            }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    updateConfig()
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {

            }
        })
    }

    private fun updateConfig() {
        kotlin.runCatching {
            val remoteConfig = Firebase.remoteConfig
            putBooleanToSP(remoteConfig, NameRemoteAdmob.INTER_SPLASH)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.INTER_HOME)
//            putBooleanToSP(remoteConfig, NameRemoteAdmob.INTER_CATEGORY)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.NATIVE_LANGUAGE)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.NATIVE_ONBOARD)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.NATIVE_EXIT)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.NATIVE_PERMISSION)
//            putBooleanToSP(remoteConfig, NameRemoteAdmob.NATIVE_FULL_SCREEN)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.APP_RESUME)
//            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_COLAPSE)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_SPLASH)
//            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_SPLASH)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_ALL)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_COLLAPSE_HOME)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_COLLAPSE_PICK_IMAGE)
            putBooleanToSP(remoteConfig, NameRemoteAdmob.BANNER_COLLAPSE_PREVIEW)

            configNativeAdmobUI(remoteConfig)
        }
    }

    private fun configNativeAdmobUI(remoteConfig: FirebaseRemoteConfig) {
        val configString = remoteConfig.getString("ui_native_admob")
        if(configString.isNotEmpty()){
            val configJson = JSONObject(configString)
            putStringToSP(configJson, NameRemoteUINative.BG_CTA)
            putStringToSP(configJson, NameRemoteUINative.TEXT_CTA)
            putStringToSP(configJson, NameRemoteUINative.BG_AD)
            putStringToSP(configJson, NameRemoteUINative.TEXT_AD)
        }
    }

    private fun putStringToSP(configJson: JSONObject, name: String) {
        val spManager = App.instance?.applicationContext?.let { SpManager.getInstance(it) }
        val values = configJson.getString(name)
        spManager?.putString(name, values)
        Log.i(TAG, "$name : $values")
    }

    private fun putBooleanToSP(remoteConfig: FirebaseRemoteConfig, name: String) {
        val spManager = App.instance?.applicationContext?.let { SpManager.getInstance(it) }
        val values = remoteConfig.getBoolean(name)
        spManager?.putBoolean(name, values)
        Log.i(TAG, "$name : $values")
    }

}