package com.solodroid.ads.sdk.format;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AppOpenAd extends com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({com.google.android.gms.ads.appopen.AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            com.google.android.gms.ads.appopen.AppOpenAd.APP_OPEN_AD_ORIENTATION_LANDSCAPE})
    public @interface AdOrientation {

    }

    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = 0L, to = MAX_AD_EXPIRY_DURATION)
    public @interface AdExpiryDuration {

    }

    public static final long MAX_AD_EXPIRY_DURATION = 3600000 * 4;
    private static final String TAG = "AppOpenManager";
    private final Application application;
    private final int orientation;
    private final long adExpiryDuration;
    private final AdRequest adRequest;
    private Activity mostCurrentActivity;
    private com.google.android.gms.ads.appopen.AppOpenAd ad;
    private boolean isShowingAd = false;
    private long lastAdFetchTime = 0L;

    public static class Builder {

        private final Application application;

        @AdOrientation
        private int orientation = com.google.android.gms.ads.appopen.AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT;

        @AdExpiryDuration
        private long adExpiryDuration = MAX_AD_EXPIRY_DURATION;

        private AdRequest adRequest = new AdRequest.Builder().build();

        public Builder(@NonNull Application application) {
            this.application = application;
        }

        public Builder setOrientation(@AdOrientation int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setAdExpiryDuration(@AdExpiryDuration long duration) {
            this.adExpiryDuration = duration;
            return this;
        }

        public Builder setAdRequest(@NonNull AdRequest request) {
            this.adRequest = request;
            return this;
        }

        public AppOpenAd build() {
            return new AppOpenAd(this);
        }
    }

    private AppOpenAd(Builder builder) {
        this.application = builder.application;
        this.orientation = builder.orientation;
        this.adExpiryDuration = builder.adExpiryDuration;
        this.adRequest = builder.adRequest;

        this.application.registerActivityLifecycleCallbacks(this);
    }

    public void showAdIfAvailable(String appOpenAdUnitId) {
        showAdIfAvailable(appOpenAdUnitId, null);
    }

    public void showAdIfAvailable(String appOpenAdUnitId, @Nullable final FullScreenContentCallback listener) {
        if (this.isShowingAd) {
            Log.e(TAG, "Can't show the ad: Already showing the ad");
            return;
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "Can't show the ad: Ad not available");
            fetchAd(appOpenAdUnitId);
            return;
        }

        FullScreenContentCallback callback = new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                if (listener != null) {
                    listener.onAdFailedToShowFullScreenContent(error);
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                if (listener != null) {
                    listener.onAdShowedFullScreenContent();
                }
                AppOpenAd.this.isShowingAd = true;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                if (listener != null) {
                    listener.onAdDismissedFullScreenContent();
                }
                isShowingAd = false;
                AppOpenAd.this.ad = null;
                fetchAd(appOpenAdUnitId);
            }
        };

        ad.setFullScreenContentCallback(callback);
        ad.show(mostCurrentActivity);
    }

    private void fetchAd(String appOpenAdUnitId) {
        if (isAdAvailable()) {
            return;
        }

        com.google.android.gms.ads.appopen.AppOpenAd.load(application, appOpenAdUnitId, adRequest, orientation, this);
    }

    private boolean isAdAvailable() {
        return this.ad != null && !isAdExpired();
    }

    private boolean isAdExpired() {
        return System.currentTimeMillis() - lastAdFetchTime > adExpiryDuration;
    }

    // AppOpenAd.AppOpenAdLoadCallback implementations
    @Override
    public void onAdLoaded(@NonNull com.google.android.gms.ads.appopen.AppOpenAd appOpenAd) {
        Log.d(TAG, "Ad loaded");
        this.lastAdFetchTime = System.currentTimeMillis();
        this.ad = appOpenAd;
    }

    @Override
    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
        Log.d(TAG, "Failed to load an ad: " + loadAdError.getMessage());
    }

    // Application.ActivityLifecycleCallbacks implementations
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        // Do nothing
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        this.mostCurrentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.mostCurrentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // Do nothing
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // Do nothing
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // Do nothing
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // Do nothing
    }

}
