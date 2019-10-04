package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class Notice extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    @SerializedName("releaseTime")
    private String date;
    private String title;
    private String content;

    public Notice(){

    }

    public Notice(String internetID, String date, String title, String content)
    {
        this.internetID = internetID;
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
