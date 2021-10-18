package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.FavouriteAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityFavouriteBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref.SharedPrefrance;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.ConnectionDetector;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class FavouriteActivity extends BaseActivity {

    ActivityFavouriteBinding favouriteBinding;
    MyClickHandlers myClickHandlers;
    FavouriteAdapter favouriteAdapter;
    ArrayList<NewResponseItem> favouriteList;
    private AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GridLayoutManager newVideoLayoutManager;

    private void fireAnalyticsAds(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favouriteBinding = DataBindingUtil.setContentView(FavouriteActivity.this, R.layout.activity_favourite);
        myClickHandlers = new MyClickHandlers(FavouriteActivity.this);
        favouriteBinding.setOnClick(myClickHandlers);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(FavouriteActivity.this);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Step 1 - Create an AdView and set the ad unit ID on it.
                    adView = new AdView(FavouriteActivity.this);
                    fireAnalyticsAds("admob_banner", "Ad Request send");
                    adView.setAdUnitId(getString(R.string.banner_ad));
                    favouriteBinding.bannerContainer.addView(adView);
                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            fireAnalyticsAds("admob_banner", "loaded");
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            if (loadAdError.getMessage() != null)
                                fireAnalyticsAds("admob_banner_Error", loadAdError.getMessage());
                        }
                    });
                    loadBanner();
                }
            }, 2000);
        }
    }

    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().build();
        AdSize adSize = AdSize.BANNER;
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isInternetPresent) {
            favouriteBinding.rlNoInternet.setVisibility(View.GONE);
            favouriteList = new ArrayList<>();
            favouriteList = SharedPrefrance.getFavouriteFileList(FavouriteActivity.this);
            Log.e("LLL_Fav: ", String.valueOf(favouriteList.size()));
            if (favouriteList.size() > 0) {
                favouriteBinding.llNoData.setVisibility(View.GONE);
            } else {
                favouriteBinding.llNoData.setVisibility(View.VISIBLE);
            }
            newVideoLayoutManager = new GridLayoutManager(FavouriteActivity.this, 2);
            favouriteBinding.rvVideos.setLayoutManager(newVideoLayoutManager);
            favouriteAdapter = new FavouriteAdapter(favouriteList, FavouriteActivity.this);
            favouriteBinding.rvVideos.setAdapter(favouriteAdapter);
        } else {
            Toasty.error(FavouriteActivity.this, "Please Check your internet connection.", Toasty.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.NAV_SELECTED_POS = 0;
        Constant.isFav = false;
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onBackBtnClicked(View view) {
            onBackPressed();
        }
    }

}