package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.PlayVideoActivity;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListPotraitImageViewBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref.SharedPrefrance;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class TrendingVideoAdapter extends RecyclerView.Adapter<TrendingVideoAdapter.VideoViewHolder> {

    public static final int AD_TYPE = 1;
    public static final int ITEM = 0;
    public final static int FIRST_ADS_ITEM_POSITION = 6;
    private final int ADS_FREQUENCY = 7;
    private final FirebaseAnalytics mFirebaseAnalytics;
    private final List<NativeAd> mNativeAds = new ArrayList<>();
    public List<Object> objects = new ArrayList<>();
    int no_of_ad_request;
    ArrayList<NewResponseItem> TrendingResponseItems;
    Activity activity;

    public TrendingVideoAdapter(ArrayList<NewResponseItem> TrendingResponseItems, Activity activity) {
        this.TrendingResponseItems = TrendingResponseItems;
        this.activity = activity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListPotraitImageViewBinding potraitImageViewBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_potrait_image_view, parent, false);
        return new VideoViewHolder(potraitImageViewBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        holder.potraitImageViewBinding.mImage.setClipToOutline(true);

        NewResponseItem itemsItem = TrendingResponseItems.get(position);
        holder.potraitImageViewBinding.setNewResponseItem(itemsItem);

        ArrayList<NewResponseItem> favFileList = SharedPrefrance.getFavouriteFileList(activity);
        if (favFileList.size() <= 0) {
            favFileList = new ArrayList<>();
        }
        for (int i = 0; i < favFileList.size(); i++) {
            ArrayList<NewResponseItem> finalFavFileList = SharedPrefrance.getFavouriteFileList(activity);
            if (finalFavFileList.size() <= 0) {
                finalFavFileList = new ArrayList<>();
            }
            if (favFileList.get(i).getId() == TrendingResponseItems.get(position).getId()) {
                Log.e("LLLL_Fav: ", String.valueOf(favFileList.get(i).getId() == (TrendingResponseItems.get(position).getId())));
                holder.potraitImageViewBinding.imgFavourite.setSelected(true);
                break;
            } else {
                holder.potraitImageViewBinding.imgFavourite.setSelected(false);
            }
        }

        ArrayList<NewResponseItem> finalFavFileList = favFileList;
        holder.potraitImageViewBinding.imgFavourite.setOnClickListener(v -> {
            if (v.isSelected()) {
                holder.potraitImageViewBinding.imgFavourite.setSelected(false);
                finalFavFileList.remove(TrendingResponseItems.get(position));
            } else {
                holder.potraitImageViewBinding.imgFavourite.setSelected(true);
                finalFavFileList.add(TrendingResponseItems.get(position));
            }
            SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
            SharedPrefrance.setFavouriteFileList(activity, finalFavFileList);
        });

        Glide
                .with(activity)
                .load(itemsItem.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.potraitImageViewBinding.sProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.potraitImageViewBinding.sProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(activity.getResources().getDrawable(R.drawable.sample))
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(holder.potraitImageViewBinding.mImage);

        holder.potraitImageViewBinding.getRoot().setOnClickListener(v -> {
            // intialize Bundle instance
            Constant.isTrending = true;

            Bundle bundle = new Bundle();
            bundle.putSerializable("VideoList", TrendingResponseItems);
            Intent intent = new Intent(activity, PlayVideoActivity.class);
            intent.putExtras(bundle);
            intent.putExtra("position", itemsItem);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return TrendingResponseItems.size();
    }

    public void addAll(ArrayList<NewResponseItem> itemsItems) {
        TrendingResponseItems.addAll(itemsItems);
        notifyItemRangeInserted(TrendingResponseItems.size() - itemsItems.size(), TrendingResponseItems.size() - 1);
    }

    public void clearAll() {
        TrendingResponseItems.clear();
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ListPotraitImageViewBinding potraitImageViewBinding;

        public VideoViewHolder(ListPotraitImageViewBinding potraitImageViewBinding) {
            super(potraitImageViewBinding.getRoot());
            this.potraitImageViewBinding = potraitImageViewBinding;
        }
    }
}
