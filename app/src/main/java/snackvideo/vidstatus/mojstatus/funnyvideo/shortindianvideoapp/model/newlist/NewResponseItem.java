package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.model.newlist;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewResponseItem implements Serializable {

    @SerializedName("love")
    private int love;

    @SerializedName("image")
    private String image;

    @SerializedName("extension")
    private String extension;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("haha")
    private int haha;

    @SerializedName("comments")
    private int comments;

    @SerializedName("like")
    private int like;

    @SerializedName("created")
    private String created;

    @SerializedName("video")
    private String video;

    @SerializedName("angry")
    private int angry;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    @SerializedName("userid")
    private int userid;

    @SerializedName("tags")
    private Object tags;

    @SerializedName("downloads")
    private int downloads;

    @SerializedName("review")
    private boolean review;

    @SerializedName("userimage")
    private String userimage;

    @SerializedName("woow")
    private int woow;

    @SerializedName("sad")
    private int sad;

    @SerializedName("comment")
    private boolean comment;

    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private String user;

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getHaha() {
        return haha;
    }

    public void setHaha(int haha) {
        this.haha = haha;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getAngry() {
        return angry;
    }

    public void setAngry(int angry) {
        this.angry = angry;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Object getTags() {
        return tags;
    }

    public void setTags(Object tags) {
        this.tags = tags;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getWoow() {
        return woow;
    }

    public void setWoow(int woow) {
        this.woow = woow;
    }

    public int getSad() {
        return sad;
    }

    public void setSad(int sad) {
        this.sad = sad;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return
                "NewResponseItem{" +
                        "love = '" + love + '\'' +
                        ",image = '" + image + '\'' +
                        ",extension = '" + extension + '\'' +
                        ",thumbnail = '" + thumbnail + '\'' +
                        ",haha = '" + haha + '\'' +
                        ",comments = '" + comments + '\'' +
                        ",like = '" + like + '\'' +
                        ",created = '" + created + '\'' +
                        ",video = '" + video + '\'' +
                        ",angry = '" + angry + '\'' +
                        ",title = '" + title + '\'' +
                        ",type = '" + type + '\'' +
                        ",userid = '" + userid + '\'' +
                        ",tags = '" + tags + '\'' +
                        ",downloads = '" + downloads + '\'' +
                        ",review = '" + review + '\'' +
                        ",userimage = '" + userimage + '\'' +
                        ",woow = '" + woow + '\'' +
                        ",sad = '" + sad + '\'' +
                        ",comment = '" + comment + '\'' +
                        ",id = '" + id + '\'' +
                        ",user = '" + user + '\'' +
                        "}";
    }
}