package com.solodroid.ads.sdk.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.solodroid.ads.sdk.R;

public class BannerAdView extends LinearLayout {

    private Context mContext;
    private AttributeSet attrs;
    private int styleAttr;
    private View view;

    public BannerAdView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public BannerAdView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.attrs = attrs;
        initView();
    }

    public BannerAdView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BannerAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView() {
        this.view = this;
        inflate(mContext, R.layout.view_banner_ad, this);
    }

}
