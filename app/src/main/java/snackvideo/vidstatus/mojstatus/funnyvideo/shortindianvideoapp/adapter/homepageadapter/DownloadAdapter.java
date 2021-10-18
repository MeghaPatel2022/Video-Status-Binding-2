package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.DownloadPlayActivity;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListPotraitImageViewBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.VideoViewHolder> {

    ArrayList<File> files;
    Activity activity;

    public DownloadAdapter(ArrayList<File> files, Activity activity) {
        this.files = files;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ListPotraitImageViewBinding potraitImageViewBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_potrait_image_view, parent, false);

        return new VideoViewHolder(potraitImageViewBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoViewHolder holder, int position) {
        holder.potraitImageViewBinding.mImage.setClipToOutline(true);
        holder.potraitImageViewBinding.imgFavourite.setVisibility(View.GONE);

        File itemsItem = files.get(position);

        holder.potraitImageViewBinding.tvTitle2.setText(itemsItem.getName());

        Glide
                .with(activity)
                .load(itemsItem.getAbsolutePath())
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

        holder.itemView.setOnClickListener(v -> {
            // intialize Bundle instance
            Constant.isFav = false;
            Constant.isTrending = false;
            Constant.isCategory = false;
            Constant.isLanguage = false;
            Intent intent = new Intent(activity, DownloadPlayActivity.class);
            intent.putExtra("position", position);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ListPotraitImageViewBinding potraitImageViewBinding;

        public VideoViewHolder(ListPotraitImageViewBinding potraitImageViewBinding) {
            super(potraitImageViewBinding.getRoot());
            this.potraitImageViewBinding = potraitImageViewBinding;
        }
    }
}
