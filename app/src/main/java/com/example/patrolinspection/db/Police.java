package com.example.patrolinspection.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

//保安
public class Police extends LitePalSupport
{
    @SerializedName("id")
    private String internetID;
    private String realName;
    private String companyId;
    private String securityCardNo;
    private String icCardNo;
    private String mainDutyId;

    private boolean officialPolice;//true表示保安卡 false表示自建（IC卡）
    private boolean inService;
    private boolean onDuty;
    private String schoolAttendanceSchoolId;

    public String getSchoolAttendanceSchoolId()
    {
        return schoolAttendanceSchoolId;
    }

    public void setSchoolAttendanceSchoolId(String schoolAttendanceSchoolId)
    {
        this.schoolAttendanceSchoolId = schoolAttendanceSchoolId;
    }

    public boolean isInService()
    {
        return inService;
    }

    public void setInService(boolean inService)
    {
        this.inService = inService;
    }

    public boolean isOnDuty()
    {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty)
    {
        this.onDuty = onDuty;
    }

    public String getInternetID()
    {
        return internetID;
    }

    public void setInternetID(String internetID)
    {
        this.internetID = internetID;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(String companyId)
    {
        this.companyId = companyId;
    }

    public String getSecurityCardNo()
    {
        return securityCardNo;
    }

    public void setSecurityCardNo(String securityCardNo)
    {
        this.securityCardNo = securityCardNo;
    }

    public String getIcCardNo()
    {
        return icCardNo;
    }

    public void setIcCardNo(String icCardNo)
    {
        this.icCardNo = icCardNo;
    }

    public String getMainDutyId()
    {
        return mainDutyId;
    }

    public void setMainDutyId(String mainDutyId)
    {
        this.mainDutyId = mainDutyId;
    }

    public boolean isOfficialPolice()
    {
        return officialPolice;
    }

    public void setOfficialPolice(boolean officialPolice)
    {
        this.officialPolice = officialPolice;
    }
}
