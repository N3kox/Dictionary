package com.example.newsFetcher;

public class NewsOverview {
    //新闻详情url
    private String newsUrl;
    //新闻标题
    private String newsTitle;
    //新闻日期
    private String newsDate;

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }
}
