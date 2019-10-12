package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

//计划 周一计划、特殊日期、自由排班
public class PatrolPlan extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String companyId;
    private String patrolPlanType;
    private String name;
    private String startDate;
    private String endDate;
    private boolean share;

    public PatrolPlan(String internetID, String companyId, String patrolPlanType, String name,
                      String startDate, String endDate, boolean share)
    {
        this.internetID = internetID;
        this.companyId = companyId;
        this.patrolPlanType = patrolPlanType;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.share = share;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getPatrolPlanType()
    {
        return patrolPlanType;
    }

    public void setPatrolPlanType(String patrolPlanType)
    {
        this.patrolPlanType = patrolPlanType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public boolean isShare()
    {
        return share;
    }

    public void setShare(boolean share)
    {
        this.share = share;
    }
}
