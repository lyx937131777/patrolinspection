package com.example.patrolinspection.db;

import org.litepal.crud.LitePalSupport;

public class Event extends LitePalSupport
{
    private int id;
    private String type;
    private String name;
    private String num;
    private String rank;
    private boolean isShare;
    private boolean isDefault;

    public Event(int id, String type, String name, String num, String rank, boolean isShare,
                 boolean isDefault)
    {
        this.id = id;
        this.type = type;
        this.name = name;
        this.num = num;
        this.rank = rank;
        this.isShare = isShare;
        this.isDefault = isDefault;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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
