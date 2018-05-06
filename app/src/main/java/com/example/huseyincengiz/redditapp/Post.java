package com.example.huseyincengiz.redditapp;

/**
 * Created by HuseyinCengiz on 30.08.2017.
 */

public class Post {
    private String title;
    private String author;
    private String data_updated;
    private String postUrl;
    private String thumbnailUrl;
    private String id;

    public Post(String title, String author, String data_updated, String postUrl, String thumbnailUrl, String id) {
        this.title = title;
        this.author = author;
        this.data_updated = data_updated;
        this.postUrl = postUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getData_updated() {
        return data_updated;
    }

    public void setData_updated(String data_updated) {
        this.data_updated = data_updated;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
