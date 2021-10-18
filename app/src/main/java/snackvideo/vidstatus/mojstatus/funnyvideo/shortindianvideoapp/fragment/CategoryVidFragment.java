package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.CategoryAdapterVid;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.homepageadapter.LanguageAdapterVid;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.databinding.FragmentCategoryVidBinding;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.category.CategoryResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.language.LanguageResponseItem;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class CategoryVidFragment extends Fragment {

    FragmentCategoryVidBinding categoryVidBinding;
    private CategoryAdapterVid categoryAdapterVid;
    private LanguageAdapterVid languageAdapterVid;

    public CategoryVidFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categoryVidBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_vid, container, false);

        categoryVidBinding.rvLanguage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        languageAdapterVid = new LanguageAdapterVid(new ArrayList<>(), getActivity());
        categoryVidBinding.rvLanguage.setAdapter(languageAdapterVid);

        categoryVidBinding.rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapterVid = new CategoryAdapterVid(new ArrayList<>(), getActivity());
        categoryVidBinding.rvCategory.setAdapter(categoryAdapterVid);
        categoryVidBinding.rvCategory.setNestedScrollingEnabled(false);

        getLanguages();
        getCategory();

        return categoryVidBinding.getRoot();
    }

    private void getLanguages() {
        ArrayList<LanguageResponseItem> languageResponseItems = new ArrayList<>();
        AndroidNetworking.get(Constant.BASEURL + "language/all/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    LanguageResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), LanguageResponseItem.class);
                                    languageResponseItems.add(newResponse);
                                }
                                languageAdapterVid.addAll(languageResponseItems);
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

    private void getCategory() {
        ArrayList<CategoryResponseItem> languageResponseItems = new ArrayList<>();
        AndroidNetworking.get(Constant.BASEURL + "category/video/all/8cve98hty47h2uf0dfg4re7fg0wdhn24ffr3er3reg67yu20/no/")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    CategoryResponseItem newResponse = new Gson().fromJson(jsonObject.toString(), CategoryResponseItem.class);
                                    languageResponseItems.add(newResponse);
                                }
                                categoryAdapterVid.addAll(languageResponseItems);
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

}
