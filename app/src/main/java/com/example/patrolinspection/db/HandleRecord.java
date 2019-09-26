package com.example.patrolinspection.db;

public class HandleRecord
{
    private int id;
    private int eventRecordID;
    private String operate;
    private int operatorID;
    private String time;
    private String img;
    private String remarks;
    private String reportUnit;

    private String operator;

    public HandleRecord(int id, int eventRecordID, String operate, int operatorID, String time,
                        String img, String remarks, String reportUnit)
    {
        this.id = id;
        this.eventRecordID = eventRecordID;
        this.operate = operate;
        this.operatorID = operatorID;
        this.time = time;
        this.img = img;
        this.remarks = remarks;
        this.reportUnit = reportUnit;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getEventRecordID()
    {
        return eventRecordID;
    }

    public void setEventRecordID(int eventRecordID)
    {
        this.eventRecordID = eventRecordID;
    }

    public String getOperate()
    {
        return operate;
    }

    public void setOperate(String operate)
    {
        this.operate = operate;
    }

    public int getOperatorID()
    {
        return operatorID;
    }

    public void setOperatorID(int operatorID)
    {
        this.operatorID = operatorID;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getImg()
    {
        return img;
    }

    public void setImg(String img)
    {
        this.img = img;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getReportUnit()
    {
        return reportUnit;
    }

    public void setReportUnit(String reportUnit)
    {
        this.reportUnit = reportUnit;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }
}
