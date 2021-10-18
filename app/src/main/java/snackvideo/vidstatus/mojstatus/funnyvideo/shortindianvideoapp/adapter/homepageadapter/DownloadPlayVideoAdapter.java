package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.BuildConfig;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListPortraitItemBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DownloadPlayVideoAdapter extends RecyclerView.Adapter<DownloadPlayVideoAdapter.MyClassView> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<File> files;
    Activity activity;
    MediaScannerConnection msConn;
    private DownloadClickListener mDownloadClickListener;

    public DownloadPlayVideoAdapter(ArrayList<File> files, Activity activity) {
        this.files = files;
        this.activity = activity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    public void downloadClickListener(DownloadClickListener listener) {
        mDownloadClickListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ListPortraitItemBinding portraitItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_portrait_item, parent, false);
        return new MyClassView(portraitItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyClassView holder, int position) {

        holder.portraitItemBinding.imgDelete.setVisibility(View.VISIBLE);
        holder.portraitItemBinding.imgDownload.setVisibility(View.GONE);
        holder.portraitItemBinding.imgFavourite.setVisibility(View.GONE);
        holder.portraitItemBinding.animationView.setVisibility(View.GONE);

        Glide.with(activity)
                .load(files.get(position).getAbsolutePath())
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(holder.portraitItemBinding.imageView);

        holder.portraitItemBinding.rlLoading.setVisibility(View.VISIBLE);
        holder.portraitItemBinding.avLoader.smoothToShow();
        holder.portraitItemBinding.imageView.setVisibility(View.VISIBLE);

        holder.portraitItemBinding.tvTile.setText(files.get(position).getName());
        Log.e("LLL_Path: ", files.get(position).getAbsolutePath());
        holder.portraitItemBinding.videoView.setVideoPath(files.get(position).getAbsolutePath());

        File file = new File(Constant.FOLDERPATH, files.get(position).getName());

        holder.portraitItemBinding.imgDelete.setOnClickListener(v -> {
            fireAnalytics("del_download_video", "StoryStatus");
            boolean isDelete = false;
            if (file.exists()) {
                isDelete = file.delete();
                if (!isDelete) {
                    isDelete = Constant.delete(activity, file);
                }
            }
            if (isDelete) {
                scanPhoto(file.toString());
                files.remove(position);
                notifyItemChanged(position);
                if (files.isEmpty()) {
                    if (mDownloadClickListener != null) {
                        mDownloadClickListener.backPress();
                    }
                }
                Toasty.success(activity, "File deleted.", Toast.LENGTH_SHORT, true).show();
            } else
                Toasty.error(activity, "Error to file delete.", Toast.LENGTH_SHORT, true).show();
        });

        holder.portraitItemBinding.imgWhatsApp.setOnClickListener(v -> {
            fireAnalytics("share_to_WhatsApp", "StoryStatus");
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent videoshare = new Intent(Intent.ACTION_SEND);
            videoshare.setType("*/*");
            videoshare.setPackage("com.whatsapp");
            videoshare.putExtra(Intent.EXTRA_TEXT, "Hey this is the video from " + activity.getResources().getString(R.string.app_name) + " app ");
            videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoshare.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(videoshare);
        });

        holder.portraitItemBinding.imgShare.setOnClickListener(v -> {
            fireAnalytics("share_video_to_other_app", "StoryStatus");
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("*/*");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hey this is the video from " + activity.getResources().getString(R.string.app_name) + " app ");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
        });

        holder.portraitItemBinding.videoView.setOnPreparedListener(mp -> {
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = holder.portraitItemBinding.videoView.getWidth() / (float)
                    holder.portraitItemBinding.videoView.getHeight();
            float scaleX = videoRatio / screenRatio;
            if (scaleX >= 1f) {
                holder.portraitItemBinding.videoView.setScaleX(scaleX);
            } else {
                holder.portraitItemBinding.videoView.setScaleY(1f / scaleX);
            }
            mp.setLooping(true);
            holder.portraitItemBinding.imageView.setVisibility(View.GONE);
            holder.portraitItemBinding.rlLoading.setVisibility(View.GONE);
            holder.portraitItemBinding.avLoader.smoothToHide();
            holder.portraitItemBinding.videoView.start();
        });

        holder.portraitItemBinding.imgBack.setOnClickListener(v -> {
            if (mDownloadClickListener != null) {
                mDownloadClickListener.backPress();
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(activity, new MediaScannerConnection.MediaScannerConnectionClient() {
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

    //Define your Interface method here
    public interface DownloadClickListener {
        void backPress();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListPortraitItemBinding portraitItemBinding;

        public MyClassView(ListPortraitItemBinding portraitItemBinding) {
            super(portraitItemBinding.getRoot());
            this.portraitItemBinding = portraitItemBinding;
        }
    }
}
