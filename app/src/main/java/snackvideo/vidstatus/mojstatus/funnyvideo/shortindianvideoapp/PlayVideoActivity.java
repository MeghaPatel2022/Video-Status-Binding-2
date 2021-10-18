package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.PlayVideoAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityPlayVideoBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref.SharedPrefrance;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.ConnectionDetector;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class PlayVideoActivity extends BaseActivity implements PlayVideoAdapter.DownloadClickListener {

    private final ArrayList<NewResponseItem> landscapeItems = new ArrayList<>();
    ActivityPlayVideoBinding playVideoBinding;
    MediaScannerConnection msConn;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    PlayVideoAdapter playVideoAdapter;
    NewResponseItem itemsItem = new NewResponseItem();
    private boolean loading = true;
    private String pageCount = "0";

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private AdView adView;
    private FirebaseAnalytics mFirebaseAnalytics;

    private void fireAnalyticsAds(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }


    @Override
    public void permissionGranted() {
        setPlayVideoAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        playVideoBinding = DataBindingUtil.setContentView(PlayVideoActivity.this, R.layout.activity_play_video);

        AndroidNetworking.initialize(PlayVideoActivity.this);
        itemsItem = (NewResponseItem) getIntent().getSerializableExtra("position");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(PlayVideoActivity.this);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Step 1 - Create an AdView and set the ad unit ID on it.
                    adView = new AdView(PlayVideoActivity.this);
                    fireAnalyticsAds("admob_banner", "Ad Request send");
                    adView.setAdUnitId(getString(R.string.banner_ad));
                    playVideoBinding.bannerContainer.addView(adView);
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
    public void onDownloadClick(int position, String imageUrl, String title, String url) {
        new LongOperation(imageUrl).execute();
        new downloadTask(landscapeItems.get(position).getTitle(), landscapeItems.get(position).getVideo(), position).execute();
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(PlayVideoActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }

    private void setPlayVideoAdapter() {
        if (Constant.isFav) {
            //get the bundle
            Bundle b = getIntent().getExtras();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayVideoActivity.this, RecyclerView.VERTICAL, false);
            playVideoBinding.rvVideos.setLayoutManager(linearLayoutManager);
            playVideoAdapter = new PlayVideoAdapter(new ArrayList<>(), PlayVideoActivity.this);
            playVideoBinding.rvVideos.setAdapter(playVideoAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            playVideoBinding.rvVideos.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(playVideoBinding.rvVideos);

            playVideoAdapter.downloadClickListener(this);
            playVideoAdapter.clearAll();
            getFavouriteList();

        } else if (Constant.isTrending) {
            //get the bundle
            Bundle b = getIntent().getExtras();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayVideoActivity.this, RecyclerView.VERTICAL, false);
            playVideoBinding.rvVideos.setLayoutManager(linearLayoutManager);
            playVideoAdapter = new PlayVideoAdapter(landscapeItems, PlayVideoActivity.this);
            playVideoBinding.rvVideos.setAdapter(playVideoAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            playVideoBinding.rvVideos.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(playVideoBinding.rvVideos);

            playVideoAdapter.downloadClickListener(this);
            playVideoAdapter.clearAll();
            getTrendingData();

            playVideoBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getTrendingData();
                                loading = true;
                            }
                        }
                    }
                }
            });
        } else if (!Constant.isCategory && !Constant.isLanguage) {
            //get the bundle
            Bundle b = getIntent().getExtras();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayVideoActivity.this, RecyclerView.VERTICAL, false);
            playVideoBinding.rvVideos.setLayoutManager(linearLayoutManager);
            playVideoAdapter = new PlayVideoAdapter(landscapeItems, PlayVideoActivity.this);
            playVideoBinding.rvVideos.setAdapter(playVideoAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            playVideoBinding.rvVideos.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(playVideoBinding.rvVideos);

            playVideoAdapter.downloadClickListener(this);
            playVideoAdapter.clearAll();
            getNewData();

            playVideoBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getNewData();
                                loading = true;
                            }
                        }
                    }
                }
            });
        } else if (Constant.isCategory) {
            Bundle b = getIntent().getExtras();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayVideoActivity.this, RecyclerView.VERTICAL, false);
            playVideoBinding.rvVideos.setLayoutManager(linearLayoutManager);
            playVideoAdapter = new PlayVideoAdapter(landscapeItems, PlayVideoActivity.this);
            playVideoBinding.rvVideos.setAdapter(playVideoAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            playVideoBinding.rvVideos.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(playVideoBinding.rvVideos);

            playVideoAdapter.downloadClickListener(this);
            playVideoAdapter.clearAll();
            getCategoryData();

            playVideoBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getCategoryData();
                                loading = true;
                            }
                        }
                    }
                }
            });
        } else {
            Bundle b = getIntent().getExtras();

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PlayVideoActivity.this, RecyclerView.VERTICAL, false);
            playVideoBinding.rvVideos.setLayoutManager(linearLayoutManager);
            playVideoAdapter = new PlayVideoAdapter(landscapeItems, PlayVideoActivity.this);
            playVideoBinding.rvVideos.setAdapter(playVideoAdapter);
            SnapHelper snapHelper = new PagerSnapHelper();
            playVideoBinding.rvVideos.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(playVideoBinding.rvVideos);

            playVideoAdapter.downloadClickListener(this);
            playVideoAdapter.clearAll();
            getLanguageData();

            playVideoBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = linearLayoutManager.getChildCount();
                        totalItemCount = linearLayoutManager.getItemCount();
                        pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getLanguageData();
                                loading = true;
                            }
                        }
                    }
                }
            });
        }
    }

    private void getFavouriteList() {
        landscapeItems.clear();
        playVideoAdapter.clearAll();
        landscapeItems.addAll(SharedPrefrance.getFavouriteFileList(PlayVideoActivity.this));
        playVideoAdapter.addAll(landscapeItems);
        for (int i = 0; i < landscapeItems.size(); i++) {
            if (landscapeItems.get(i).getId() == itemsItem.getId()) {
                int finalI = i;
                runOnUiThread(() -> playVideoBinding.rvVideos.scrollToPosition(finalI));
            }
        }
    }

    private void getTrendingData() {
        landscapeItems.clear();
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
        AndroidNetworking.get("http://port.kscreation.in/api/video/all/" + pageCount + "/downloads/0/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    NewResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), NewResponseItem.class);
                                    if (newResponse.getId() != itemsItem.getId())
                                        responseItems.add(newResponse);
                                }
                                if (pageCount.equals("0")) {
                                    landscapeItems.add(itemsItem);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                responseItems.remove(itemsItem);
                                landscapeItems.addAll(responseItems);
                                playVideoAdapter.addAll(landscapeItems);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_Error: ", anError.getErrorBody());
                    }
                });
    }

    private void getNewData() {
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
        landscapeItems.clear();
        AndroidNetworking.get("http://port.kscreation.in/api/video/all/" + pageCount + "/created/1/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    NewResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), NewResponseItem.class);
                                    if (newResponse.getId() != itemsItem.getId())
                                        responseItems.add(newResponse);
                                }
                                if (pageCount.equals("0")) {
                                    landscapeItems.add(itemsItem);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                landscapeItems.addAll(responseItems);
                                playVideoAdapter.addAll(landscapeItems);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLL_Error: ", anError.getErrorBody());
                    }
                });
    }

    private void getCategoryData() {
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
        landscapeItems.clear();
        AndroidNetworking.get("http://port.kscreation.in/api/video/by/category/" + pageCount + "/created/0/" + Constant.catId + "%2F8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20%2Fno")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    NewResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), NewResponseItem.class);
                                    if (newResponse.getId() != itemsItem.getId())
                                        responseItems.add(newResponse);
                                }
                                if (pageCount.equals("0")) {
                                    landscapeItems.add(itemsItem);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                landscapeItems.addAll(responseItems);
                                playVideoAdapter.addAll(landscapeItems);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void getLanguageData() {
        landscapeItems.clear();
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
        Log.e("LLL_URL: ", Constant.BASEURL + "video/all/" + pageCount + "/created/" + Constant.catId + "/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/");
        AndroidNetworking.get("http://port.kscreation.in/api/video/all/" + pageCount + "/created/" + Constant.catId + "/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    NewResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), NewResponseItem.class);
                                    if (newResponse.getId() != itemsItem.getId())
                                        responseItems.add(newResponse);
                                }
                                if (pageCount.equals("0")) {
                                    landscapeItems.add(itemsItem);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                landscapeItems.addAll(responseItems);
                                playVideoAdapter.addAll(landscapeItems);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("LLLL_Error: ", anError.getErrorBody());
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (!Constant.isFav) {
            Intent intent = new Intent(PlayVideoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void backPress() {
        onBackPressed();
    }

    private final class LongOperation extends AsyncTask<Void, Void, Bitmap> {

        String downloadUrl;

        public LongOperation(String url) {
            this.downloadUrl = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                playVideoBinding.sProgressBar.setImageBitmap(result);
            }
        }
    }

    private final class downloadTask extends AsyncTask<Void, Void, String> {

        private final int TIMEOUT_CONNECTION = 5000;//5sec
        private final int TIMEOUT_SOCKET = 30000;//30sec

        String fileName;
        String downloadUrl;
        int position = 0;

        public downloadTask(String fileName, String downloadUrl, int position) {
            this.fileName = fileName;
            this.downloadUrl = downloadUrl;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playVideoBinding.sProgressBar.showProgress(true);
                    Constant.expand(playVideoBinding.rlProgress);
                    playVideoBinding.tvTile1.setText("Downloading...");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            AndroidNetworking.download(downloadUrl, Constant.FOLDERPATH, fileName + ".mp4")
                    .setTag("downloadTest")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .setAnalyticsListener(new AnalyticsListener() {
                        @Override
                        public void onReceived(long timeTakenInMillis, long bytesSent, long bytesReceived, boolean isFromCache) {
                            Log.e("LLLLL_Progress: ", (timeTakenInMillis / 1000) + " Received: " + (bytesReceived / 1000) + "  Sent: " + (bytesSent / 1000));
                        }
                    })
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            // do anything with progress
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long progress = (bytesDownloaded * 100) / totalBytes;
                                    playVideoBinding.sProgressBar.setProgress(progress);
                                    Log.e("LLLLL_Progress: ", ((bytesDownloaded * 100) / totalBytes) + " Total: " + (totalBytes / 1000));
                                }
                            });

                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            // do anything after completion
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("LLLLL_Progress: ", "Download Completed.");

                                    File file = new File(Constant.FOLDERPATH, fileName + ".mp4");
                                    scanPhoto(file.toString());
                                    playVideoBinding.tvTile1.setText("Download Completed.");
                                    playVideoAdapter.notifyItemChanged(position);
                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Constant.collapse(playVideoBinding.rlProgress);
                                        }
                                    }, 2000);
                                }
                            });
                        }

                        @Override
                        public void onError(ANError error) {
                            // handle error
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("LLLLL_Progress_Err: ", error.getErrorDetail());
                                }
                            });
                        }
                    });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}