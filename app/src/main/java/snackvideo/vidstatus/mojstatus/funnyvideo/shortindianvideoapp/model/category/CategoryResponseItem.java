package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.category;

public class CategoryResponseItem {
    private String image;
    private int id;
    private String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return
                "CategoryResponseItem{" +
                        "image = '" + image + '\'' +
                        ",id = '" + id + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}
