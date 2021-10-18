package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.DrawerAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.pageradapters.ViewPagerAdapterVid;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ActivityMainBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.ConnectionDetector;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mainBinding;
    private static final int RC_APP_UPDATE = 101;
    private final ArrayList<String> menuItems = new ArrayList<>();
    public NativeAdLayout nativeAdLayout;
    MyClickHandlers myClickHandlers;
    Dialog dial;
    Dialog dial1;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    ViewPagerAdapterVid viewPagerAdapterVid;
    InterstitialAdListener interstitialAdListener;
    DrawerAdapter drawerAdapter;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView adView;
    private com.facebook.ads.NativeAd nativeAd;
    private com.facebook.ads.InterstitialAd interstitialAd;

    private AppUpdateManager mAppUpdateManager;
    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        popupSnackbarForCompleteUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (mAppUpdateManager != null) {
                            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.i("LLL_Update_App: ", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);
                    Log.e("LLL_Update_App: ", "Update available");
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.e("LLL_Update_App: ", e.getMessage());
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e("LLL_Update_App: ", "checkForAppUpdateAvailability: something else");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        mainBinding.drawerLayout,
                        "New app is ready!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.purple_200));
        snackbar.show();
    }

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
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        myClickHandlers = new MyClickHandlers(MainActivity.this);
        mainBinding.setOnClick(myClickHandlers);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(MainActivity.this, initializationStatus -> {
        });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        rateUs();

        if (isInternetPresent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Step 1 - Create an AdView and set the ad unit ID on it.
                    adView = new AdView(MainActivity.this);
                    fireAnalyticsAds("admob_banner", "Ad Request send");
                    adView.setAdUnitId(getString(R.string.banner_ad));
                    mainBinding.bannerContainer.addView(adView);
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
            interstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_inter_placementID));

            // load the ad
            fireAnalyticsAds("fb_interstitial", "Ad Request send");
            interstitialAd.loadAd();
        }

        setNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInternetPresent) {
            mainBinding.bottomNavigation.setVisibility(View.GONE);
            Toasty.error(MainActivity.this, "Please Check your internet connection.", Toasty.LENGTH_LONG).show();
        } else {
            mainBinding.rlNoInternet.setVisibility(View.GONE);
            mainBinding.bottomNavigation.setVisibility(View.VISIBLE);
            setViewPager();
            exitApp();
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

    private void exitApp() {
        dial = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dial.requestWindowFeature(1);
        dial.setContentView(R.layout.dialog_exit);
        dial.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial.setCanceledOnTouchOutside(true);
        nativeAdLayout = dial.findViewById(R.id.native_ad_container);

        loadNativeAd(MainActivity.this);

        dial.findViewById(R.id.delete_yes).setOnClickListener(view -> {
            dial.dismiss();
            finishAffinity();
        });
        dial.findViewById(R.id.delete_no).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dial.dismiss();
            }
        });
    }

    private void rateUs() {
        dial1 = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dial1.requestWindowFeature(1);
        dial1.setContentView(R.layout.dialige_rate_us);
        dial1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dial1.setCanceledOnTouchOutside(true);

        LottieAnimationView animationView = dial1.findViewById(R.id.animationView);
        animationView.playAnimation();

        dial1.findViewById(R.id.yes).setOnClickListener(view -> {
            dial1.dismiss();
            /* This code assumes you are inside an activity */
            final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
//                    final Uri uri = Uri.parse("market://details?id=com.bbotdev.weather");
            final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

            if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                startActivity(rateAppIntent);
            } else {
                /* handle your error case: the device has no way to handle market urls */
            }
        });
        dial1.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dial1.dismiss();
            }
        });
    }

    private void loadNativeAd(Context context) {

        nativeAd = new com.facebook.ads.NativeAd(context, "521075155719800_521075932386389");
        // creating  NativeAdListener
        NativeAdListener nativeAdListener = new NativeAdListener() {

            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                // showing Toast message
                fireAnalyticsAds("fb_native", "Error to load");
                Log.e("Native error:", adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                fireAnalyticsAds("fb_native", "loaded");
                Log.e("LLL_Native: ", "Loded");
                if (nativeAdLayout != null)
                    inflateAd(nativeAd, nativeAdLayout);

            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        fireAnalyticsAds("fb_native", "Ad Request send");
        // Load an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build());
    }

    void inflateAd(com.facebook.ads.NativeAd nativeAd, NativeAdLayout nativeAdLayout) {

        Log.e("LLL_Native: ", "Come");
        // Add the Ad view into the ad container.

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        // Inflate the Ad view.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.ad_unified_facebook, nativeAdLayout, false);

        // adding view
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(MainActivity.this, nativeAd, nativeAdLayout);

        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Setting  the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and  button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    @Override
    public void onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(mainBinding.navigationView)) {
            mainBinding.drawerLayout.closeDrawers();
        } else {
            if (mainBinding.viewPager.getCurrentItem() == 2) {
                mainBinding.viewPager.setCurrentItem(1);
            } else if (mainBinding.viewPager.getCurrentItem() == 1) {
                mainBinding.viewPager.setCurrentItem(0);
            } else if (mainBinding.viewPager.getCurrentItem() == 0) {
                if (dial != null)
                    dial.show();
            }
        }
    }

    private void setViewPager() {
        viewPagerAdapterVid = new ViewPagerAdapterVid(getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapterVid);

        if (!Constant.isCategory && !Constant.isLanguage) {
            viewPager.setCurrentItem(Constant.pagerPosition);
            runOnUiThread(() -> drawerAdapter.notifyDataSetChanged());
            mainBinding.tvTitle.setText(viewPagerAdapterVid.getPageTitle(Constant.pagerPosition));
        } else {
            Constant.pagerPosition = 0;
            viewPager.setCurrentItem(0);
            Constant.NAV_SELECTED_POS = 0;
            runOnUiThread(() -> drawerAdapter.notifyDataSetChanged());
            mainBinding.tvTitle.setText(viewPagerAdapterVid.getPageTitle(0));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                mainBinding.tvTitle.setText(viewPagerAdapterVid.getPageTitle(position));
                Constant.pagerPosition = position;
                if (position == 0) {
                    Constant.BOTTOM_SELECTED_ITEM = "Latest";
                } else if (position == 1) {
                    Constant.BOTTOM_SELECTED_ITEM = "Trending";
                }

                switch (position) {
                    case 0:
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.bottomNavigationNewId).setChecked(true);
                        break;
                    case 1:
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.bottomNavigationTrendingId).setChecked(true);
                        break;
                    case 2:
                        mainBinding.bottomNavigation.getMenu().findItem(R.id.bottomNavigationCategoryId).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.bottomNavigationNewId:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.bottomNavigationTrendingId:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.bottomNavigationCategoryId:
                    viewPager.setCurrentItem(2);
                    break;
            }
            return true;
        });

        viewPager.setOffscreenPageLimit(3);
    }

    private void setNavigation() {

        menuItems.add("Home");
        menuItems.add("Favourite");
        menuItems.add("Download");
        menuItems.add("Share App");
        menuItems.add("About Us");
        menuItems.add("Privacy Policy");
        menuItems.add("Rate US");
        menuItems.add("More");

        drawerAdapter = new DrawerAdapter(this, menuItems);

        mainBinding.navDesign.navList.setAdapter(drawerAdapter);
        mainBinding.navDesign.navList.setOnItemClickListener((parent, view, position, id) -> {
            mainBinding.drawerLayout.closeDrawers();
            Constant.NAV_SELECTED_POS = position;
            drawerAdapter.notifyDataSetChanged();
            switch (position) {
                case 0:
                    break;
                case 1:
                    interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            fireAnalyticsAds("fb_interstitial", "Error to load");
                            Log.e("LLLL_ErrFB: ", adError.getErrorMessage());
                            Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            fireAnalyticsAds("fb_interstitial", "loaded");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            // load the ad
                            interstitialAd.loadAd();

                            Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                            startActivity(intent);
                        }
                    };
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build();
                    if (interstitialAd.isAdLoaded())
                        interstitialAd.show();
                    else {
                        Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                        startActivity(intent);
                    }

                    break;
                case 2:
                    interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            fireAnalyticsAds("fb_interstitial", "Error to load");
                            Log.e("LLLL_ErrFB: ", adError.getErrorMessage());
                            Intent intent1 = new Intent(MainActivity.this, DownloadActivity.class);
                            startActivity(intent1);
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            fireAnalyticsAds("fb_interstitial", "loaded");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {

                            // load the ad
                            interstitialAd.loadAd();

                            Intent intent1 = new Intent(MainActivity.this, DownloadActivity.class);
                            startActivity(intent1);
                        }
                    };
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build();
                    if (interstitialAd.isAdLoaded())
                        interstitialAd.show();
                    else {
                        Intent intent1 = new Intent(MainActivity.this, DownloadActivity.class);
                        startActivity(intent1);
                    }

                    break;
                case 3:
                    try {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                        String shareMessage = "\nLet me recommend you this application\n\n";
                        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                        startActivity(Intent.createChooser(shareIntent, "choose one"));
                    } catch (Exception e) {
                        Toasty.error(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    break;
                case 4:
                    Intent intent = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(intent);
                    break;
                case 5:
                    Intent intent1 = new Intent(MainActivity.this, PrivacyPolicy.class);
                    startActivity(intent1);
                    break;
                case 6:
                    if (dial1 != null)
                        dial1.show();
                    break;
                case 7:
                    Toasty.info(MainActivity.this, "Coming soon...", Toasty.LENGTH_LONG).show();
                    break;
            }

            if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onMenuClicked(View view) {
            mainBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}