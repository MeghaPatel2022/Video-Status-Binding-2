package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.language;

import com.google.gson.annotations.SerializedName;

public class LanguageResponseItem {

    @SerializedName("image")
    private String image;

    @SerializedName("language")
    private String language;

    @SerializedName("id")
    private int id;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "LanguageResponseItem{" +
                        "image = '" + image + '\'' +
                        ",language = '" + language + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}