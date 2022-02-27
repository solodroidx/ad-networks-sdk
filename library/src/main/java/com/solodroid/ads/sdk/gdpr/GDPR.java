package com.solodroid.ads.sdk.gdpr;

import android.app.Activity;
import android.util.Log;

import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class GDPR {

    private ConsentInformation consentInformation;
    ConsentForm consentForm;
    Activity activity;

    public GDPR(Activity activity) {
        this.activity = activity;
    }

    public void updateGDPRConsentStatus() {
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(activity, params, () -> {
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm(activity);
                    }
                },
                formError -> {
                });
        Log.d("GDPR", "GDPR Funding choices is selected");
    }

    public void loadForm(Activity activity) {
        UserMessagingPlatform.loadConsentForm(activity, consentForm -> {
                    this.consentForm = consentForm;
                    if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(activity, formError -> {
                            loadForm(activity);
                        });
                    }
                },
                formError -> {
                }
        );
    }

}
