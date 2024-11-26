package com.msc.ar_drawing.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.msc.ar_drawing.admob.BaseAdmob;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class OpenAdmob extends BaseAdmob {
    private static final String TAG = "openAdmob";

    private final String id;
    private boolean isLoadingAd = false;

    private AppOpenAd appOpenAd = null;
    private long loadTime = 0;
    private Activity currentActivity;

    public OpenAdmob(Context context, String id) {
        super(context);
        this.id = id;
    }

    public void loadAd(Activity activity){
        loadAd(activity, null);
    }

    public void loadAd(Activity context, BaseAdmob.OnAdmobLoadListener onAdmobLoadListener){
        Log.i(TAG, "loadAd: ");

        if (isLoadingAd || isAdAvailable()) {
            Log.i(TAG, "isLoadingAd || isAdAvailable(): ");
            return;
        }
        isLoadingAd = true;
        AppOpenAd.load(
                context,
                id,
                adRequestBuilder.build(),
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        Log.i(TAG, "onAdLoaded: ");
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();

                        ad.setOnPaidEventListener(adValue -> {
                            AppEventsLogger.newLogger(context).logPurchase(BigDecimal.valueOf(adValue.getValueMicros()/1000000.0f*25000f), Currency.getInstance("VND"));
                        });

                        if(onAdmobLoadListener != null) onAdmobLoadListener.onLoad();
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.i(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        isLoadingAd = false;
                        if(onAdmobLoadListener != null) onAdmobLoadListener.onError(loadAdError.getMessage());
                    }
                });
    }

    private boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public void showAdIfAvailable(@NonNull final Activity activity, BaseAdmob.OnAdmobShowListener onAdmobShowListener){
        Log.i(TAG, "showAdIfAvailable: " + activity.getClass().getSimpleName());
        if (isShowingOpenAd || !canShowOpenApp) {
            Log.i(TAG, "isShowingOpenAd || !CAN_SHOW_OPEN_APP: ");
            return;
        }
        if (!isAdAvailable()) {
            Log.i(TAG, "!isAdAvailable(): ");
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.i(TAG, "onAdDismissedFullScreenContent: ");
                        latestTimeShowOpenAd = System.currentTimeMillis();
                        appOpenAd = null;
                        isShowingOpenAd = false;
                        if(onAdmobShowListener != null) onAdmobShowListener.onShow();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.i(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        appOpenAd = null;
                        isShowingOpenAd = false;
                        if(onAdmobShowListener != null) onAdmobShowListener.onError(adError.getMessage());
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.i(TAG, "onAdShowedFullScreenContent: ");
                        isShowingOpenAd = true;
                    }
                });

        appOpenAd.show(activity);
    }

    public void showAdIfAvailable(@NonNull final Activity activity) {
        showAdIfAvailable(activity, null);
    }
}
