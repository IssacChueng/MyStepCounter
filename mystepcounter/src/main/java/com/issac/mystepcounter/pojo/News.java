package com.issac.mystepcounter.pojo;

/**
 * Created by zhans on 2016/11/11.
 */

public class News {
    private String title;
    private String date;
    private String source;
    private String url;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    private String picUrl;

    public News() {
    }

    public News(String title, String date, String source, String url) {
        this.title = title;
        this.date = date;
        this.source = source;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
