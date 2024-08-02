package com.msc.ar_drawing.utils

import android.annotation.SuppressLint
import com.msc.ar_drawing.App
import com.msc.ar_drawing.BuildConfig
import com.msc.ar_drawing.admob.NameRemoteAdmob
import com.msc.ar_drawing.admob.NativeAdmob

class NativeAdmobUtils {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var languageNativeAdmobDefault: NativeAdmob? = null
        @SuppressLint("StaticFieldLeak")
        var languageNativeAdmobS2: NativeAdmob? = null

        @SuppressLint("StaticFieldLeak")
        var onboardNativeAdmob1: NativeAdmob? = null
        @SuppressLint("StaticFieldLeak")
        var onboardNativeAdmob2: NativeAdmob? = null
        @SuppressLint("StaticFieldLeak")
        var onboardNativeAdmob3: NativeAdmob? = null
        @SuppressLint("StaticFieldLeak")
        var onboardFullNativeAdmob: NativeAdmob? = null

        @SuppressLint("StaticFieldLeak")
        var permissionNativeAdmob: NativeAdmob? = null

        @SuppressLint("StaticFieldLeak")
        var nativeExitLiveData: NativeAdmob? = null

        fun loadNativeLanguage() {
            App.instance?.applicationContext?.let { context ->
                languageNativeAdmobDefault = NativeAdmob(
                    context,
                    BuildConfig.native_lanugage_s2_all_price
                )
                languageNativeAdmobDefault?.load(null)

                languageNativeAdmobS2 = NativeAdmob(
                    context,
                    BuildConfig.native_lanugage_s2
                )
                languageNativeAdmobS2?.load(null)
            }
        }
        fun loadNativePermission() {
            App.instance?.applicationContext?.let { context ->
                if(SpManager.getInstance(context).getBoolean(NameRemoteAdmob.NATIVE_PERMISSION, true)){
                    permissionNativeAdmob = NativeAdmob(
                        context,
                        BuildConfig.native_permission
                    )
                    permissionNativeAdmob?.load(null)
                }
            }
        }

        fun loadNativeOnboard() {
            App.instance?.applicationContext?.let {context ->
                onboardNativeAdmob1 = NativeAdmob(
                    context,
                    BuildConfig.native_onboarding
                )
                onboardNativeAdmob1?.load(null)

                onboardNativeAdmob2 = NativeAdmob(
                    context,
                    BuildConfig.native_onboarding
                )
                onboardNativeAdmob2?.load(null)

                onboardNativeAdmob3 = NativeAdmob(
                    context,
                    BuildConfig.native_onboarding
                )
                onboardNativeAdmob3?.load(null)

                onboardFullNativeAdmob = NativeAdmob(
                    context,
                    BuildConfig.native_full_screen
                )
                onboardFullNativeAdmob?.load(null)
            }
        }

        fun loadNativeExit(){
            App.instance?.applicationContext?.let {context ->
                nativeExitLiveData = NativeAdmob(
                    context,
                    BuildConfig.native_exit
                )
                nativeExitLiveData?.load(null)
            }
        }
    }
}