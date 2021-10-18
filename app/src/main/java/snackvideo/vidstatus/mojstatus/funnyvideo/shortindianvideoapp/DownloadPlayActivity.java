package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.DownloadPlayVideoAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityDownloadPlayBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.ConnectionDetector;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class DownloadPlayActivity extends BaseActivity implements DownloadPlayVideoAdapter.DownloadClickListener {

    private final ArrayList<NewResponseItem> landscapeItems = new ArrayList<>();
    ActivityDownloadPlayBinding downloadPlayBinding;
    int position = 0;
    DownloadPlayVideoAdapter downloadAdapter;
    ArrayList<File> downloadList;
    private AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    @Override
    public void permissionGranted() {

    }

    private void fireAnalyticsAds(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        downloadPlayBinding = DataBindingUtil.setContentView(DownloadPlayActivity.this, R.layout.activity_download_play);

        position = getIntent().getIntExtra("position", 0);

        downloadList = new ArrayList<>();
        downloadList.addAll(getAllFilesFromStorage());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DownloadPlayActivity.this, RecyclerView.VERTICAL, false);
        downloadPlayBinding.rvVideos.setLayoutManager(linearLayoutManager);
        downloadAdapter = new DownloadPlayVideoAdapter(downloadList, DownloadPlayActivity.this);
        downloadPlayBinding.rvVideos.setAdapter(downloadAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        downloadPlayBinding.rvVideos.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(downloadPlayBinding.rvVideos);
        downloadPlayBinding.rvVideos.scrollToPosition(position);

        downloadAdapter.downloadClickListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(DownloadPlayActivity.this);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Step 1 - Create an AdView and set the ad unit ID on it.
                    adView = new AdView(DownloadPlayActivity.this);
                    fireAnalyticsAds("admob_banner", "Ad Request send");
                    adView.setAdUnitId(getString(R.string.banner_ad));
                    downloadPlayBinding.bannerContainer.addView(adView);
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

    private ArrayList<File> getAllFilesFromStorage() {
        ArrayList<File> fileArrayList = new ArrayList<>();
        Log.d("Files", "Path: " + Constant.FOLDERPATH);
        File directory = new File(Constant.FOLDERPATH);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        if (files.length > 0) {
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
    public void backPress() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}