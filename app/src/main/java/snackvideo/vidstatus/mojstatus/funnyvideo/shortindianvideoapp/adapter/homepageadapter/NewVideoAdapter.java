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

import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.PlayVideoActivity;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListPotraitImageViewBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref.SharedPrefrance;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewVideoAdapter extends RecyclerView.Adapter<NewVideoAdapter.VideoViewHolder> {

    ArrayList<NewResponseItem> newResponseItems;
    Activity activity;

    public NewVideoAdapter(ArrayList<NewResponseItem> newResponseItems, Activity activity) {
        this.newResponseItems = newResponseItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListPotraitImageViewBinding imageViewBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_potrait_image_view, parent, false);
        return new VideoViewHolder(imageViewBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        ArrayList<NewResponseItem> favFileList = SharedPrefrance.getFavouriteFileList(activity);
        if (favFileList.size() <= 0) {
            favFileList = new ArrayList<>();
        }
        for (int i = 0; i < favFileList.size(); i++) {
            if (favFileList.get(i).getId() == newResponseItems.get(position).getId()) {
                Log.e("LLLL_Fav: ", String.valueOf(favFileList.get(i).getId() == (newResponseItems.get(position).getId())));
                holder.imageViewBinding.imgFavourite.setSelected(true);
                break;
            } else {
                holder.imageViewBinding.imgFavourite.setSelected(false);
            }
        }

        holder.imageViewBinding.imgFavourite.setOnClickListener(v -> {
            ArrayList<NewResponseItem> finalFavFileList = SharedPrefrance.getFavouriteFileList(activity);
            if (finalFavFileList.size() <= 0) {
                finalFavFileList = new ArrayList<>();
            }
            if (v.isSelected()) {
                holder.imageViewBinding.imgFavourite.setSelected(false);
                finalFavFileList.remove(newResponseItems.get(position));
            } else {
                holder.imageViewBinding.imgFavourite.setSelected(true);
                finalFavFileList.add(newResponseItems.get(position));
            }
            SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
            SharedPrefrance.setFavouriteFileList(activity, finalFavFileList);
        });

        holder.imageViewBinding.mImage.setClipToOutline(true);

        NewResponseItem itemsItem = newResponseItems.get(position);

        holder.imageViewBinding.tvTitle2.setText(itemsItem.getTitle());

        Log.e("LLL_Image: ",itemsItem.getImage());

        Glide
                .with(activity)
                .load(itemsItem.getImage())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.imageViewBinding.sProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imageViewBinding.sProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(activity.getResources().getDrawable(R.drawable.sample))
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(holder.imageViewBinding.mImage);

        holder.imageViewBinding.getRoot().setOnClickListener(v -> {
            // intialize Bundle instance
            Constant.isFav = false;
            Constant.isCategory = false;
            Constant.isTrending = false;
            Bundle bundle = new Bundle();
            bundle.putSerializable("VideoList", newResponseItems);
            Intent intent = new Intent(activity, PlayVideoActivity.class);
            intent.putExtras(bundle);
            intent.putExtra("position", itemsItem);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    @Override
    public int getItemCount() {
        return newResponseItems.size();
    }

    public void addAll(ArrayList<NewResponseItem> itemsItems) {
        newResponseItems.addAll(itemsItems);
        notifyItemRangeInserted(newResponseItems.size() - itemsItems.size(), newResponseItems.size() - 1);
    }

    public void clearAll() {
        newResponseItems.clear();
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ListPotraitImageViewBinding imageViewBinding;

        public VideoViewHolder(ListPotraitImageViewBinding imageViewBinding) {
            super(imageViewBinding.getRoot());
            this.imageViewBinding = imageViewBinding;
        }
    }
}
