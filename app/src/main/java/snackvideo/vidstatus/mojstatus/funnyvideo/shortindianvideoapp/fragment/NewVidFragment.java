package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.NewVideoAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.FragmentNewVidBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class NewVidFragment extends Fragment {

    FragmentNewVidBinding newVidBinding;
    MyClickHandlers myClickHandlers;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    Animation animFadeOut, animFadeIn;
    NewVideoAdapter newVideoAdapter;
    private boolean loading = true;
    private String pageCount = "0";
    private GridLayoutManager newVideoLayoutManager;


    public NewVidFragment() {

    }

    private void avLoaderVisible() {
        newVidBinding.rlLoading.setVisibility(View.VISIBLE);
        newVidBinding.avLoader.smoothToShow();
    }

    private void avLoaderGone() {
        newVidBinding.avLoader.smoothToHide();
        newVidBinding.rlLoading.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        newVidBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_vid, container, false);
        myClickHandlers = new MyClickHandlers(getActivity());
        newVidBinding.setOnClick(myClickHandlers);

        AndroidNetworking.initialize(getContext());

        animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);

        return newVidBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter() {
        if (!Constant.isCategory && !Constant.isLanguage) {
            newVideoLayoutManager = new GridLayoutManager(getContext(), 2);
            newVidBinding.rvVideos.setLayoutManager(newVideoLayoutManager);
            newVideoAdapter = new NewVideoAdapter(new ArrayList<>(), getActivity());
            newVidBinding.rvVideos.setAdapter(newVideoAdapter);

            newVideoAdapter.clearAll();
            pageCount = "0";
            getNewData();

            newVidBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                        animFadeIn.reset();
                        newVidBinding.imgFirst.clearAnimation();
                        newVidBinding.imgFirst.startAnimation(animFadeIn);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newVidBinding.imgFirst.startAnimation(animFadeOut);
                                newVidBinding.imgFirst.setVisibility(View.GONE);
                            }
                        }, 3000);

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getNewData();
                                loading = true;
                            }
                        }
                    } else {
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        if (totalItemCount >= 10 && pastVisibleItems >= 10) {

                            newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                            animFadeIn.reset();
                            newVidBinding.imgFirst.clearAnimation();
                            newVidBinding.imgFirst.startAnimation(animFadeIn);

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    newVidBinding.imgFirst.startAnimation(animFadeOut);
                                    newVidBinding.imgFirst.setVisibility(View.GONE);
                                }
                            }, 3000);
                        }
                    }
                }
            });
        } else if (Constant.isCategory) {
            newVideoLayoutManager = new GridLayoutManager(getContext(), 2);
            newVidBinding.rvVideos.setLayoutManager(newVideoLayoutManager);
            newVideoAdapter = new NewVideoAdapter(new ArrayList<>(), getActivity());
            newVidBinding.rvVideos.setAdapter(newVideoAdapter);

            newVideoAdapter.clearAll();
            pageCount = "0";
            getCategoryData();

            newVidBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                        animFadeIn.reset();
                        newVidBinding.imgFirst.clearAnimation();
                        newVidBinding.imgFirst.startAnimation(animFadeIn);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newVidBinding.imgFirst.startAnimation(animFadeOut);
                                newVidBinding.imgFirst.setVisibility(View.GONE);
                            }
                        }, 3000);

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getCategoryData();
                                loading = true;
                            }
                        }
                    } else {
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        if (totalItemCount >= 10 && pastVisibleItems >= 10) {

                            newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                            animFadeIn.reset();
                            newVidBinding.imgFirst.clearAnimation();
                            newVidBinding.imgFirst.startAnimation(animFadeIn);

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    newVidBinding.imgFirst.startAnimation(animFadeOut);
                                    newVidBinding.imgFirst.setVisibility(View.GONE);
                                }
                            }, 3000);
                        }
                    }
                }
            });
        } else {
            newVideoLayoutManager = new GridLayoutManager(getContext(), 2);
            newVidBinding.rvVideos.setLayoutManager(newVideoLayoutManager);
            newVideoAdapter = new NewVideoAdapter(new ArrayList<>(), getActivity());
            newVidBinding.rvVideos.setAdapter(newVideoAdapter);

            newVideoAdapter.clearAll();
            pageCount = "0";
            getLanguageData();

            newVidBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                        animFadeIn.reset();
                        newVidBinding.imgFirst.clearAnimation();
                        newVidBinding.imgFirst.startAnimation(animFadeIn);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newVidBinding.imgFirst.startAnimation(animFadeOut);
                                newVidBinding.imgFirst.setVisibility(View.GONE);
                            }
                        }, 3000);

                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                Log.v("...", "Last Item Wow !");
                                // Do pagination.. i.e. fetch new data
                                getLanguageData();
                                loading = true;
                            }
                        }
                    } else {
                        visibleItemCount = newVideoLayoutManager.getChildCount();
                        totalItemCount = newVideoLayoutManager.getItemCount();
                        pastVisibleItems = newVideoLayoutManager.findFirstVisibleItemPosition();

                        if (totalItemCount >= 10 && pastVisibleItems >= 10) {

                            newVidBinding.imgFirst.setVisibility(View.VISIBLE);
                            animFadeIn.reset();
                            newVidBinding.imgFirst.clearAnimation();
                            newVidBinding.imgFirst.startAnimation(animFadeIn);

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    newVidBinding.imgFirst.startAnimation(animFadeOut);
                                    newVidBinding.imgFirst.setVisibility(View.GONE);
                                }
                            }, 3000);
                        }
                    }
                }
            });
        }
    }

    private void getNewData() {
        avLoaderVisible();
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
        Log.e("LLL_URL: ", Constant.BASEURL + "video/all/" + pageCount + "/created/1/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/");
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
                                    responseItems.add(newResponse);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                newVideoAdapter.addAll(responseItems);
                                if (getActivity() != null)
                                    getActivity().runOnUiThread(() -> avLoaderGone());
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
        avLoaderVisible();
        ArrayList<NewResponseItem> responseItems = new ArrayList<>();
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
                                    responseItems.add(newResponse);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                newVideoAdapter.addAll(responseItems);
                                getActivity().runOnUiThread(() -> avLoaderGone());
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
        avLoaderVisible();
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
                                    responseItems.add(newResponse);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                newVideoAdapter.addAll(responseItems);
                            }
                            getActivity().runOnUiThread(() -> avLoaderGone());
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

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onFirstBtnClicked(View view) {
            newVidBinding.rvVideos.smoothScrollToPosition(0);
        }
    }

}

