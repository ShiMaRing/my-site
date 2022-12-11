package com.xgs.dto;

import com.xgs.model.ContentDomain;

import java.util.List;

/**
 * 文章归档类
 * Created by xgs on 2022/11/30.
 */
public class ArchiveDto {

    private String date;
    private String count;
    private List<ContentDomain> articles;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<ContentDomain> getArticles() {
        return articles;
    }

    public void setArticles(List<ContentDomain> articles) {
        this.articles = articles;
    }
}
