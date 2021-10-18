package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.ListVidCategoryBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.category.CategoryResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CategoryAdapterVid extends RecyclerView.Adapter<CategoryAdapterVid.MyClassView> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<CategoryResponseItem> categoryItems = new ArrayList<>();
    Activity activity;

    public CategoryAdapterVid(ArrayList<CategoryResponseItem> categoryItems, Activity activity) {
        this.categoryItems = categoryItems;
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
        ListVidCategoryBinding vidCategoryBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_vid_category, parent, false);
        return new MyClassView(vidCategoryBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {
        holder.vidCategoryBinding.mImage.setClipToOutline(true);
        holder.vidCategoryBinding.setCatItems(categoryItems.get(position));
        Glide
                .with(activity)
                .load(categoryItems.get(position).getImage())
                .error(activity.getResources().getDrawable(R.drawable.cat_sample))
                .transition(withCrossFade())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(holder.vidCategoryBinding.mImage);

        holder.itemView.setOnClickListener(v -> {
            Constant.isLanguage = false;
            Constant.isCategory = true;
            Constant.NAV_SELECTED_ITEM = categoryItems.get(position).getTitle();
            Constant.catId = categoryItems.get(position).getId();
            fireAnalytics("select_category", categoryItems.get(position).getTitle());
            /*Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();*/
        });
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public void addAll(ArrayList<CategoryResponseItem> itemsItems) {
        categoryItems.clear();
        categoryItems.addAll(itemsItems);
        notifyDataSetChanged();
    }

    public void clearAll() {
        categoryItems.clear();
        notifyDataSetChanged();
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        private final ListVidCategoryBinding vidCategoryBinding;

        public MyClassView(ListVidCategoryBinding vidCategoryBinding) {
            super(vidCategoryBinding.getRoot());
            this.vidCategoryBinding = vidCategoryBinding;
        }
    }
}
