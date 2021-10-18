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
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.TrendingVideoAdapter;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.FragmentTrendingVidBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;

public class TrendingVidFragment extends Fragment {

    FragmentTrendingVidBinding trendingVidBinding;
    MyClickHandlers myClickHandlers;

    int pastVisibleItems, visibleItemCount, totalItemCount;
    Animation animFadeOut, animFadeIn;
    GridLayoutManager gridLayoutManager1;
    TrendingVideoAdapter trendingVideoAdapter;
    private boolean loading = true;
    private String pageCount = "1";
    private GridLayoutManager storyGridLayoutManager;

    public TrendingVidFragment() {
        // Required empty public constructor
    }

    private void avLoaderVisible() {
        trendingVidBinding.rlLoading.setVisibility(View.VISIBLE);
        trendingVidBinding.avLoader.smoothToShow();
    }

    private void avLoaderGone() {
        trendingVidBinding.avLoader.smoothToHide();
        trendingVidBinding.rlLoading.setVisibility(View.GONE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        trendingVidBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trending_vid, container, false);
        myClickHandlers = new MyClickHandlers(getContext());
        trendingVidBinding.setOnClick(myClickHandlers);

        AndroidNetworking.initialize(getContext());

        animFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);

        return trendingVidBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter() {
        gridLayoutManager1 = new GridLayoutManager(getContext(), 2);
        trendingVidBinding.rvVideos.setLayoutManager(gridLayoutManager1);
        trendingVideoAdapter = new TrendingVideoAdapter(new ArrayList<>(), getActivity());
        trendingVidBinding.rvVideos.setAdapter(trendingVideoAdapter);

        trendingVideoAdapter.clearAll();
        pageCount = "1";
        getNewData();

        trendingVidBinding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = gridLayoutManager1.getChildCount();
                    totalItemCount = gridLayoutManager1.getItemCount();
                    pastVisibleItems = gridLayoutManager1.findFirstVisibleItemPosition();

                    trendingVidBinding.imgFirst.setVisibility(View.VISIBLE);
                    animFadeIn.reset();
                    trendingVidBinding.imgFirst.clearAnimation();
                    trendingVidBinding.imgFirst.startAnimation(animFadeIn);

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            trendingVidBinding.imgFirst.startAnimation(animFadeOut);
                            trendingVidBinding.imgFirst.setVisibility(View.GONE);
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
                    visibleItemCount = gridLayoutManager1.getChildCount();
                    totalItemCount = gridLayoutManager1.getItemCount();
                    pastVisibleItems = gridLayoutManager1.findFirstVisibleItemPosition();

                    if (totalItemCount >= 10 && pastVisibleItems >= 10) {

                        trendingVidBinding.imgFirst.setVisibility(View.VISIBLE);
                        animFadeIn.reset();
                        trendingVidBinding.imgFirst.clearAnimation();
                        trendingVidBinding.imgFirst.startAnimation(animFadeIn);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                trendingVidBinding.imgFirst.startAnimation(animFadeOut);
                                trendingVidBinding.imgFirst.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                }
            }
        });
    }

    private void getNewData() {
        avLoaderVisible();
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
                                    NewResponseItem trendingResponseItem = new Gson().fromJson(jsonObject.toString(), NewResponseItem.class);
                                    responseItems.add(trendingResponseItem);
                                }
                                pageCount = String.valueOf(Integer.parseInt(pageCount) + 1);
                                trendingVideoAdapter.addAll(responseItems);
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

    public class MyClickHandlers {
        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onFirstBtnClicked(View view) {
            trendingVidBinding.rvVideos.smoothScrollToPosition(0);
        }
    }
}
