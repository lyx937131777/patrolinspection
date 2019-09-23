package com.example.patrolinspection.db;

import java.io.Serializable;

public class PatrolInspection
{
    private String name;
    private String state;
    private String startTime;
    private String endTime;
    private String duringTime;
    private String realStartTime;
    private String realEndTime;

    public PatrolInspection(String name, String state, String startTime, String endTime,
                            String duringTime)
    {
        this.name = name;
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duringTime = duringTime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getDuringTime()
    {
        return duringTime;
    }

    public void setDuringTime(String duringTime)
    {
        this.duringTime = duringTime;
    }

    public String getRealStartTime()
    {
        return realStartTime;
    }

    public void setRealStartTime(String realStartTime)
    {
        this.realStartTime = realStartTime;
    }

    public String getRealEndTime()
    {
        return realEndTime;
    }

    public void setRealEndTime(String realEndTime)
    {
        this.realEndTime = realEndTime;
    }
}
