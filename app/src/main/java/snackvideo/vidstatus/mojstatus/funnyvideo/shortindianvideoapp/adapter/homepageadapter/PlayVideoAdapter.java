package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.animation.Animator;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.DoubleClickListener;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListPortraitItemBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref.SharedPrefrance;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class PlayVideoAdapter extends RecyclerView.Adapter<PlayVideoAdapter.MyClassView> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<NewResponseItem> landscapeItems;
    Activity activity;
    private DownloadClickListener mDownloadClickListener;

    public PlayVideoAdapter(ArrayList<NewResponseItem> landscapeItems, Activity activity) {
        this.landscapeItems = landscapeItems;
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
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListPortraitItemBinding portraitItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_portrait_item, parent, false);
        return new MyClassView(portraitItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView viewHolder, int position) {

        ArrayList<NewResponseItem> favFileList = SharedPrefrance.getFavouriteFileList(activity);
        if (favFileList.size() <= 0) {
            favFileList = new ArrayList<>();
        }
        for (int i = 0; i < favFileList.size(); i++) {
            Log.e("LLLL_Fav: ", String.valueOf(favFileList.get(i).getId() == (landscapeItems.get(position).getId())));
            if (favFileList.get(i).getId() == (landscapeItems.get(position).getId())) {
                viewHolder.portraitItemBinding.imgFavourite.setSelected(true);
                break;
            }
        }

        Glide.with(activity)
                .load(landscapeItems.get(position).getImage())
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(viewHolder.portraitItemBinding.imageView);

        viewHolder.portraitItemBinding.rlLoading.setVisibility(View.VISIBLE);
        viewHolder.portraitItemBinding.avLoader.smoothToShow();
        viewHolder.portraitItemBinding.imageView.setVisibility(View.VISIBLE);

        viewHolder.portraitItemBinding.tvTile.setText(landscapeItems.get(position).getTitle());

        Uri uri = Uri.parse(landscapeItems.get(position).getVideo());

        viewHolder.portraitItemBinding.videoView.setVideoURI(uri);

        File file = new File(Constant.FOLDERPATH, landscapeItems.get(position).getTitle() + ".mp4");
        if (!file.exists()) {
            viewHolder.portraitItemBinding.imgDownload.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_download));
        } else {
            viewHolder.portraitItemBinding.imgDownload.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_alredy_download));
        }

        viewHolder.portraitItemBinding.imgDownload.setOnClickListener(v -> {
            fireAnalytics("download_video", "StoryStatus");
            if (mDownloadClickListener != null) {
                if (!file.exists())
                    mDownloadClickListener.onDownloadClick(position, landscapeItems.get(position).getImage(), landscapeItems.get(position).getTitle(), landscapeItems.get(position).getVideo());
                else
                    Toasty.success(activity, "Already Downloaded.", Toast.LENGTH_SHORT, true).show();
            }
        });

        viewHolder.portraitItemBinding.imgWhatsApp.setOnClickListener(v -> {
            fireAnalytics("share_to_WhatsApp", "StoryStatus");
            Constant.whatsappShareVideo(activity, landscapeItems.get(position).getVideo(), file);
        });

        viewHolder.portraitItemBinding.imgShare.setOnClickListener(v -> {
            fireAnalytics("share_video_to_other_app", "StoryStatus");
            Constant.shareVideo(activity, landscapeItems.get(position).getVideo(), file);
        });

        viewHolder.portraitItemBinding.videoView.setOnPreparedListener(mp -> {
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = viewHolder.portraitItemBinding.videoView.getWidth() / (float)
                    viewHolder.portraitItemBinding.videoView.getHeight();
            float scaleX = videoRatio / screenRatio;
            if (scaleX >= 1f) {
                viewHolder.portraitItemBinding.videoView.setScaleX(scaleX);
            } else {
                viewHolder.portraitItemBinding.videoView.setScaleY(1f / scaleX);
            }
            mp.setLooping(true);
            viewHolder.portraitItemBinding.imageView.setVisibility(View.GONE);
            viewHolder.portraitItemBinding.rlLoading.setVisibility(View.GONE);
            viewHolder.portraitItemBinding.avLoader.smoothToHide();
            viewHolder.portraitItemBinding.videoView.start();
        });

        viewHolder.portraitItemBinding.animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                activity.runOnUiThread(() -> viewHolder.portraitItemBinding.animationView.setVisibility(View.GONE));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ArrayList<NewResponseItem> finalFavFileList = favFileList;
        viewHolder.portraitItemBinding.imgFavourite.setOnClickListener(v -> {

            if (v.isSelected()) {
                ArrayList<NewResponseItem> favFileList1 = SharedPrefrance.getFavouriteFileList(activity);
                viewHolder.portraitItemBinding.imgFavourite.setSelected(false);
                for (int i = 0; i < favFileList1.size(); i++) {
                    Log.e("LLLL_Fav: ", String.valueOf(favFileList1.get(i).getId() == (landscapeItems.get(position).getId())));
                    if (favFileList1.get(i).getId() == (landscapeItems.get(position).getId())) {
                        favFileList1.remove(i);
                        SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
                        SharedPrefrance.setFavouriteFileList(activity, favFileList1);
                        break;
                    }
                }

                if (Constant.isFav)
                    landscapeItems.remove(position);

                if (landscapeItems.size() <= 0) {
                    if (mDownloadClickListener != null) {
                        mDownloadClickListener.backPress();
                    }
                } else {
                    if (Constant.isFav)
                        notifyItemRemoved(position);
                }
            } else {
                viewHolder.portraitItemBinding.animationView.setVisibility(View.VISIBLE);
                viewHolder.portraitItemBinding.animationView.playAnimation();
                viewHolder.portraitItemBinding.imgFavourite.setSelected(true);
                finalFavFileList.add(landscapeItems.get(position));
                SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
                SharedPrefrance.setFavouriteFileList(activity, finalFavFileList);

            }

        });

        ArrayList<NewResponseItem> finalFavFileList1 = favFileList;
        viewHolder.itemView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                viewHolder.portraitItemBinding.animationView.setVisibility(View.VISIBLE);
                viewHolder.portraitItemBinding.animationView.playAnimation();

                if (finalFavFileList.size() > 0) {
                    for (int i = 0; i < finalFavFileList1.size(); i++) {
                        if (finalFavFileList1.get(i).getId() != (landscapeItems.get(position).getId())) {
                            viewHolder.portraitItemBinding.animationView.setVisibility(View.VISIBLE);
                            viewHolder.portraitItemBinding.animationView.playAnimation();
                            viewHolder.portraitItemBinding.imgFavourite.setSelected(true);
                            finalFavFileList1.add(landscapeItems.get(position));
                        }
                        SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
                        SharedPrefrance.setFavouriteFileList(activity, finalFavFileList1);
                    }
                } else {
                    viewHolder.portraitItemBinding.animationView.setVisibility(View.VISIBLE);
                    viewHolder.portraitItemBinding.animationView.playAnimation();
                    viewHolder.portraitItemBinding.imgFavourite.setSelected(true);
                    finalFavFileList1.add(landscapeItems.get(position));
                    SharedPrefrance.setFavouriteFileList(activity, new ArrayList<>());
                    SharedPrefrance.setFavouriteFileList(activity, finalFavFileList1);
                }
            }
        });

        viewHolder.portraitItemBinding.imgBack.setOnClickListener(v -> {
            if (mDownloadClickListener != null) {
                mDownloadClickListener.backPress();
            }
        });
    }

    @Override
    public int getItemCount() {
        return landscapeItems.size();
    }

    public void addAll(ArrayList<NewResponseItem> itemsItems) {
        landscapeItems.addAll(itemsItems);
        notifyDataSetChanged();
        Log.e("LLL_size: ", itemsItems.size() + "    : " + landscapeItems.size());
//        notifyItemRangeInserted(landscapeItems.size() - itemsItems.size(), landscapeItems.size() - 1);
    }

    public void clearAll() {
        landscapeItems.clear();
        notifyDataSetChanged();
    }

    //Define your Interface method here
    public interface DownloadClickListener {
        void onDownloadClick(int position, String imageUrl, String title, String url);

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
