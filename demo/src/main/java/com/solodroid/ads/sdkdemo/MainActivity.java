package com.solodroid.ads.sdkdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;

public class MainActivity extends AppCompatActivity {

    public static final String AD_STATUS = "1";
    public static final String AD_NETWORK = "admob";

    public static final String ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String ADMOB_NATIVE_ID = "ca-app-pub-3940256099942544/2247696110";

    public static final String STARTAPP_APP_ID = "0";

    public static final String UNITY_GAME_ID = "4089993";
    public static final String UNITY_BANNER_ID = "banner";
    public static final String UNITY_INTERSTITIAL_ID = "interstitial";

    public static final String APPLOVIN_BANNER_ID = "f32d1355463ae430";
    public static final String APPLOVIN_INTERSTITIAL_ID = "9665529a164fec6c";

    public static final String MOPUB_BANNER_ID = "b195f8dd8ded45fe847ad89ed1d016da";
    public static final String MOPUB_INTERSTITIAL_ID = "24534e1901884e398f1253216226017e";

    Toolbar toolbar;
    AdNetwork.Initialize adNetwork;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adNetwork = new AdNetwork.Initialize(this)
                .setAdStatus(AD_STATUS)
                .setAdNetwork(AD_NETWORK)
                .setAdMobAppId(null)
                .setStartappAppId(STARTAPP_APP_ID)
                .setUnityGameId(UNITY_GAME_ID)
                .setAppLovinSdkKey(null)
                .setMopubBannerId(MOPUB_BANNER_ID)
                .setDebug(true)
                .build();

        bannerAd = new BannerAd.Builder(this)
                .setAdStatus(AD_STATUS)
                .setAdNetwork(AD_NETWORK)
                .setAdMobBannerId(ADMOB_BANNER_ID)
                .setUnityBannerId(UNITY_BANNER_ID)
                .setAppLovinBannerId(APPLOVIN_BANNER_ID)
                .setMopubBannerId(MOPUB_BANNER_ID)
                .setDarkTheme(false)
                .build();

        interstitialAd = new InterstitialAd.Builder(this)
                .setAdStatus(AD_STATUS)
                .setAdNetwork(AD_NETWORK)
                .setAdMobInterstitialId(ADMOB_INTERSTITIAL_ID)
                .setUnityInterstitialId(UNITY_INTERSTITIAL_ID)
                .setAppLovinInterstitialId(APPLOVIN_INTERSTITIAL_ID)
                .setMopubInterstitialId(MOPUB_INTERSTITIAL_ID)
                .setInterval(1)
                .build();

        findViewById(R.id.btn_interstitial).setOnClickListener(v -> interstitialAd.show());

        nativeAd = new NativeAd.Builder(this)
                .setAdStatus(AD_STATUS)
                .setAdNetwork(AD_NETWORK)
                .setAdMobNativeId(ADMOB_NATIVE_ID)
                .setDarkTheme(false)
                .build();

    }

}