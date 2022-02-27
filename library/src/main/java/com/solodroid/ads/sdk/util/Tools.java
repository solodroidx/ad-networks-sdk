package com.solodroid.ads.sdk.util;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.ads.mediation.facebook.FacebookExtras;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.solodroid.ads.sdk.gdpr.GDPR;
import com.solodroid.ads.sdk.gdpr.LegacyGDPR;

public class Tools {

    public static AdSize getAdSize(Activity activity) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public static AdRequest getAdRequest(Activity activity, Boolean legacyGDPR) {
        Bundle extras = new FacebookExtras().setNativeBanner(true).build();
        if (legacyGDPR) {
            return new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, LegacyGDPR.getBundleAd(activity))
                    .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
        } else {
            return new AdRequest.Builder()
                    .addNetworkExtrasBundle(FacebookAdapter.class, extras)
                    .build();
        }
    }

}
