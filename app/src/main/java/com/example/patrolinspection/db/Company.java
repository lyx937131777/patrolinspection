package com.example.patrolinspection.db;

public class Company
{
    private boolean isschool;
    private boolean isface;
    private boolean isappAttendance;
    private boolean isschoolLogin;
    private String longitude;
    private String latitude;
    private String floor;
    private String hight;

    public boolean isIsschoolLogin()
    {
        return isschoolLogin;
    }

    public void setIsschoolLogin(boolean isschoolLogin)
    {
        this.isschoolLogin = isschoolLogin;
    }

    public boolean isIsschool()
    {
        return isschool;
    }

    public void setIsschool(boolean isschool)
    {
        this.isschool = isschool;
    }

    public boolean isIsface()
    {
        return isface;
    }

    public void setIsface(boolean isface)
    {
        this.isface = isface;
    }

    public boolean isIsappAttendance()
    {
        return isappAttendance;
    }

    public void setIsappAttendance(boolean isappAttendance)
    {
        this.isappAttendance = isappAttendance;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getFloor()
    {
        return floor;
    }

    public void setFloor(String floor)
    {
        this.floor = floor;
    }

    public String getHight()
    {
        return hight;
    }

    public void setHight(String hight)
    {
        this.hight = hight;
    }
}
