package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.pref;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.collection.ArraySet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist.NewResponseItem;


public class SharedPrefrance {
    public static final String MyPREFERENCES = "VidStatus";
    public static String FAVOURITE_LIST = "Favourite_file_list";
    public static String DOWNLOAD_LIST = "Download_list";

    public static ArrayList<NewResponseItem> getFavouriteFileList(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sharedpreferences.getString(FAVOURITE_LIST, "");
        ArrayList<NewResponseItem> lstArrayList = gson.fromJson(response,
                new TypeToken<List<NewResponseItem>>() {
                }.getType());
        if (lstArrayList == null)
            lstArrayList = new ArrayList<>();
        return lstArrayList;
    }

    public static void setFavouriteFileList(Context c1, ArrayList<NewResponseItem> hideFileList) {
        Gson gson = new Gson();
        String json = gson.toJson(hideFileList);

        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(FAVOURITE_LIST, json);
        edit.commit();
    }

    public static ArrayList<String> getDownloadList(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet(DOWNLOAD_LIST, new ArraySet<>());
        ArrayList<String> sample = new ArrayList<>(set);
        return sample;
    }

    public static void setDownloadList(Context c1, ArrayList<String> hideFileList) {
        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(hideFileList);
        edit.putStringSet(DOWNLOAD_LIST, set);
        edit.commit();
    }

}
