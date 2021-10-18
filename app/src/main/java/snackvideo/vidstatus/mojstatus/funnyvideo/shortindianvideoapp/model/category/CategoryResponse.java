package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.category;

import java.util.List;

public class CategoryResponse {
    private List<CategoryResponseItem> categoryResponse;

    public List<CategoryResponseItem> getCategoryResponse() {
        return categoryResponse;
    }

    public void setCategoryResponse(List<CategoryResponseItem> categoryResponse) {
        this.categoryResponse = categoryResponse;
    }

    @Override
    public String toString() {
        return
                "CategoryResponse{" +
                        "categoryResponse = '" + categoryResponse + '\'' +
                        "}";
    }
}