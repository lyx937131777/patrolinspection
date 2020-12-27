package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

//事件
public class Event extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String companyId;
    @SerializedName("eventType")
    private String type;
    @SerializedName("eventName")
    private String name;
    @SerializedName("eventNo")
    private String num;
    @SerializedName("eventRank")
    private String rank;
    @SerializedName("share")
    private boolean isShare;
    @SerializedName("isdefault")
    private boolean isDefault;

    public Event(){

    }
    public Event(String internetID, String type, String name, String num, String rank, boolean isShare,
                 boolean isDefault)
    {
        this.internetID = internetID;
        this.type = type;
        this.name = name;
        this.num = num;
        this.rank = rank;
        this.isShare = isShare;
        this.isDefault = isDefault;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNum()
    {
        return num;
    }

    public void setNum(String num)
    {
        this.num = num;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public boolean isShare()
    {
        return isShare;
    }

    public void setShare(boolean share)
    {
        isShare = share;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean aDefault)
    {
        isDefault = aDefault;
    }
}
