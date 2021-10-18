package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.language;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LanguageResponse {

    @SerializedName("LanguageResponse")
    private List<LanguageResponseItem> languageResponse;

    public List<LanguageResponseItem> getLanguageResponse() {
        return languageResponse;
    }

    public void setLanguageResponse(List<LanguageResponseItem> languageResponse) {
        this.languageResponse = languageResponse;
    }

    @Override
    public String toString() {
        return
                "LanguageResponse{" +
                        "languageResponse = '" + languageResponse + '\'' +
                        "}";
    }
}