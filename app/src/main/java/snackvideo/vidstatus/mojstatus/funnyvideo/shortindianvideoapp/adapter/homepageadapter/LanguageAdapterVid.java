package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.MainActivity;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListVidLangugeBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.language.LanguageResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;


public class LanguageAdapterVid extends RecyclerView.Adapter<LanguageAdapterVid.MyClassView> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<LanguageResponseItem> languageItems;
    Activity activity;

    public LanguageAdapterVid(ArrayList<LanguageResponseItem> languageItems, Activity activity) {
        this.languageItems = languageItems;
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

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListVidLangugeBinding vidLangugeBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_vid_languge, parent, false);
        return new MyClassView(vidLangugeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        holder.langugeBinding.setLanguageItems(languageItems.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.isCategory = true;
                if (v.isSelected()) {
                    v.setSelected(false);
                    holder.langugeBinding.tvLanguage.setTextColor(activity.getResources().getColor(R.color.purple_200));
                } else {
                    v.setSelected(true);
                    holder.langugeBinding.tvLanguage.setTextColor(activity.getResources().getColor(R.color.black));
                }
            }
        });

        holder.langugeBinding.getRoot().setOnClickListener(v -> {
            fireAnalytics("select_category", languageItems.get(position).getLanguage());
            Constant.isCategory = false;
            Constant.isLanguage = true;
            if (position != 0)
                Constant.NAV_SELECTED_ITEM = languageItems.get(position).getLanguage();
            else
                Constant.NAV_SELECTED_ITEM = "New";
            Constant.catId = languageItems.get(position).getId();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void addAll(ArrayList<LanguageResponseItem> itemsItems) {
        languageItems.clear();
        languageItems.addAll(itemsItems);
        notifyDataSetChanged();
    }

    public void clearAll() {
        languageItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return languageItems.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListVidLangugeBinding langugeBinding;

        public MyClassView(ListVidLangugeBinding langugeBinding) {
            super(langugeBinding.getRoot());
            this.langugeBinding = langugeBinding;
        }
    }
}
