package com.solodroid.ads.sdk.format;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.MOPUB;
import static com.solodroid.ads.sdk.util.Constant.STARTAPP;
import static com.solodroid.ads.sdk.util.Constant.UNITY;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.solodroid.ads.sdk.util.Tools;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

import java.util.concurrent.TimeUnit;

public class InterstitialAd {

    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private com.google.android.gms.ads.interstitial.InterstitialAd adMobInterstitialAd;
        private StartAppAd startAppAd;
        private MaxInterstitialAd maxInterstitialAd;
        public MoPubInterstitial mInterstitial;
        private int retryAttempt;
        private int counter = 1;

        private String adStatus = "";
        private String adNetwork = "";
        private String adMobInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String mopubInterstitialId = "";
        private int placementStatus = 1;
        private int interval = 3;

        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public InterstitialAd.Builder build() {
            loadInterstitialAd();
            return this;
        }

        public void show() {
            showInterstitialAd();
        }

        public InterstitialAd.Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public InterstitialAd.Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public InterstitialAd.Builder setAdMobInterstitialId(String adMobInterstitialId) {
            this.adMobInterstitialId = adMobInterstitialId;
            return this;
        }

        public InterstitialAd.Builder setUnityInterstitialId(String unityInterstitialId) {
            this.unityInterstitialId = unityInterstitialId;
            return this;
        }

        public InterstitialAd.Builder setAppLovinInterstitialId(String appLovinInterstitialId) {
            this.appLovinInterstitialId = appLovinInterstitialId;
            return this;
        }

        public InterstitialAd.Builder setMopubInterstitialId(String mopubInterstitialId) {
            this.mopubInterstitialId = mopubInterstitialId;
            return this;
        }

        public InterstitialAd.Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public InterstitialAd.Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public InterstitialAd.Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                        com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId, Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                adMobInterstitialAd = interstitialAd;
                                adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        loadInterstitialAd();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        Log.d(TAG, "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        adMobInterstitialAd = null;
                                        Log.d(TAG, "The ad was shown.");
                                    }
                                });
                                Log.i(TAG, "onAdLoaded");
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.i(TAG, loadAdError.getMessage());
                                adMobInterstitialAd = null;
                                Log.d(TAG, "Failed load AdMob Interstitial Ad");
                            }
                        });
                        break;

                    case STARTAPP:
                        startAppAd = new StartAppAd(activity);
                        break;

                    case APPLOVIN:
                        maxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                        maxInterstitialAd.setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                retryAttempt = 0;
                                Log.d(TAG, "AppLovin Interstitial Ad loaded...");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                maxInterstitialAd.loadAd();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {

                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                retryAttempt++;
                                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                                new Handler().postDelayed(() -> maxInterstitialAd.loadAd(), delayMillis);
                                Log.d(TAG, "failed to load AppLovin Interstitial");
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                maxInterstitialAd.loadAd();
                            }
                        });

                        // Load the first ad
                        maxInterstitialAd.loadAd();
                        break;

                    case MOPUB:
                        mInterstitial = new MoPubInterstitial(activity, mopubInterstitialId);
                        mInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
                            @Override
                            public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
                                Log.d(TAG, "Mopub Interstitial Ad is ready");
                            }

                            @Override
                            public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
                                Log.d(TAG, "failed to load Mopub Interstitial Ad");
                            }

                            @Override
                            public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

                            }

                            @Override
                            public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

                            }

                            @Override
                            public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
                                mInterstitial.load();
                            }
                        });
                        mInterstitial.load();
                        break;
                }
            }
        }

        public void showInterstitialAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                        if (adMobInterstitialAd != null) {
                            if (counter == interval) {
                                adMobInterstitialAd.show(activity);
                                counter = 1;
                            } else {
                                counter++;
                            }
                        }
                        Log.d(TAG, "admob show");
                        break;

                    case STARTAPP:
                        if (counter == interval) {
                            startAppAd.showAd();
                            counter = 1;
                        } else {
                            counter++;
                        }
                        break;

                    case UNITY:
                        if (counter == interval) {
                            UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

                                }

                                @Override
                                public void onUnityAdsShowStart(String s) {

                                }

                                @Override
                                public void onUnityAdsShowClick(String s) {

                                }

                                @Override
                                public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                                }
                            });
                            counter = 1;
                        } else {
                            counter++;
                        }
                        break;

                    case APPLOVIN:
                        if (maxInterstitialAd.isReady()) {
                            Log.d(TAG, "ready : " + counter);
                            if (counter == interval) {
                                maxInterstitialAd.showAd();
                                counter = 1;
                                Log.d(TAG, "show ad");
                            } else {
                                counter++;
                            }
                        }
                        break;

                    case MOPUB:
                        if (mInterstitial.isReady()) {
                            if (counter == interval) {
                                mInterstitial.show();
                                counter = 1;
                            } else {
                                counter++;
                            }
                        }
                        Log.d(TAG, "show " + adNetwork + " Interstitial Id : " + mopubInterstitialId);
                        Log.d(TAG, "counter : " + counter);
                        break;
                }
            }
        }

    }

}
