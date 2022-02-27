package com.solodroid.ads.sdk.gdpr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import java.net.MalformedURLException;
import java.net.URL;

public class LegacyGDPR {

    Activity activity;

    public LegacyGDPR(Activity activity) {
        this.activity = activity;
    }

    public static Bundle getBundleAd(Activity activity) {
        Bundle extras = new Bundle();
        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        if (consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
            extras.putString("npa", "1");
        }
        return extras;
    }

    public void updateLegacyGDPRConsentStatus(String adMobPublisherId, String privacyPolicyUrl) {
        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);
        // for debug needed
        //consentInformation.addTestDevice("6E03755720167250AEBF7573B4E86B62");
        //consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        consentInformation.requestConsentInfoUpdate(new String[]{adMobPublisherId}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated. Display the consent consentForm if Consent Status is UNKNOWN
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    new GDPRForm(activity).displayConsentForm(privacyPolicyUrl);
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // Consent consentForm error.
                Log.e("GDPR", errorDescription);

            }
        });
        Log.d("GDPR", "Legacy GDPR is selected");
    }

    private static class GDPRForm {

        private ConsentForm consentForm;
        Activity activity;

        private GDPRForm(Activity activity) {
            this.activity = activity;
        }

        private void displayConsentForm(String privacyPolicyUrl) {
            ConsentForm.Builder builder = new ConsentForm.Builder(activity, getUrlPrivacyPolicy(privacyPolicyUrl));
            builder.withPersonalizedAdsOption();
            builder.withNonPersonalizedAdsOption();
            builder.withListener(new ConsentFormListener() {
                @Override
                public void onConsentFormLoaded() {
                    // Consent consentForm loaded successfully.
                    consentForm.show();
                }

                @Override
                public void onConsentFormOpened() {
                    // Consent consentForm was displayed.
                }

                @Override
                public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                    // Consent consentForm was closed.
                    Log.e("GDPR", "Status : " + consentStatus);
                }

                @Override
                public void onConsentFormError(String errorDescription) {
                    // Consent consentForm error.
                    Log.e("GDPR", errorDescription);
                }
            });
            consentForm = builder.build();
            consentForm.load();
        }

        private URL getUrlPrivacyPolicy(String privacyPolicyUrl) {
            URL mUrl = null;
            try {
                mUrl = new URL(privacyPolicyUrl);
            } catch (MalformedURLException e) {
                Log.e("GDPR", e.getMessage());
            }
            return mUrl;
        }
    }


}
