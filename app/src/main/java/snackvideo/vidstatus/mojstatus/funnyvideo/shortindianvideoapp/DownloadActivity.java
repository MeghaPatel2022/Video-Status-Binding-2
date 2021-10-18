package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.DownloadAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityDownloadBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.ConnectionDetector;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class DownloadActivity extends BaseActivity {

    ActivityDownloadBinding downloadBinding;
    MyClickHandlers myClickHandlers;

    ArrayList<File> downloadList;
    private AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GridLayoutManager downloadLayoutManager;
    private DownloadAdapter downloadAdapter;

    private void fireAnalyticsAds(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    public void permissionGranted() {
        downloadList = new ArrayList<>();
        downloadList.addAll(getAllFilesFromStorage());
        if (downloadList.size() > 0) {
            downloadBinding.llNoData.setVisibility(View.GONE);
        } else {
            downloadBinding.llNoData.setVisibility(View.VISIBLE);
        }
        downloadLayoutManager = new GridLayoutManager(DownloadActivity.this, 2);
        downloadBinding.rvVideos.setLayoutManager(downloadLayoutManager);
        downloadAdapter = new DownloadAdapter(downloadList, DownloadActivity.this);
        downloadBinding.rvVideos.setAdapter(downloadAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadBinding = DataBindingUtil.setContentView(DownloadActivity.this, R.layout.activity_download);
        myClickHandlers = new MyClickHandlers(DownloadActivity.this);
        downloadBinding.setOnClick(myClickHandlers);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(DownloadActivity.this);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Step 1 - Create an AdView and set the ad unit ID on it.
                    adView = new AdView(DownloadActivity.this);
                    fireAnalyticsAds("admob_banner", "Ad Request send");
                    adView.setAdUnitId(getString(R.string.banner_ad));
                    downloadBinding.bannerContainer.addView(adView);
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
    }

    private ArrayList<File> getAllFilesFromStorage() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File directory = new File(Constant.FOLDERPATH);
        if (!directory.exists())
            directory.mkdirs();

        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (!file.getName().startsWith(".")) {
                        fileArrayList.add(file);
                    }
                }
            }
        }

        return fileArrayList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.NAV_SELECTED_POS = 0;
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