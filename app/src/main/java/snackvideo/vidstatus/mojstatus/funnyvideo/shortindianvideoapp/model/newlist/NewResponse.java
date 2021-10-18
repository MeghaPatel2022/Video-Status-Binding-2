package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewResponse {

    @SerializedName("NewResponse")
    private List<NewResponseItem> newResponse;

    public List<NewResponseItem> getNewResponse() {
        return newResponse;
    }

    public void setNewResponse(List<NewResponseItem> newResponse) {
        this.newResponse = newResponse;
    }

    @Override
    public String toString() {
        return
                "NewResponse{" +
                        "newResponse = '" + newResponse + '\'' +
                        "}";
    }
}