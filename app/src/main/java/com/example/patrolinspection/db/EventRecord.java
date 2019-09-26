package com.example.patrolinspection.db;

public class EventRecord
{
    private int id;
    private String operate;
    private int operatorID;
    private String startTime;
    private String latestTime;
    private int eventID;
    private int PatrolRecordID;
    private int informationPointID;

    public EventRecord(int id, String operate, int operatorID, String startTime,
                       String latestTime, int eventID, int patrolRecordID, int informationPointID)
    {
        this.id = id;
        this.operate = operate;
        this.operatorID = operatorID;
        this.startTime = startTime;
        this.latestTime = latestTime;
        this.eventID = eventID;
        PatrolRecordID = patrolRecordID;
        this.informationPointID = informationPointID;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getLatestTime()
    {
        return latestTime;
    }

    public void setLatestTime(String latestTime)
    {
        this.latestTime = latestTime;
    }

    public int getEventID()
    {
        return eventID;
    }

    public void setEventID(int eventID)
    {
        this.eventID = eventID;
    }

    public int getPatrolRecordID()
    {
        return PatrolRecordID;
    }

    public void setPatrolRecordID(int patrolRecordID)
    {
        PatrolRecordID = patrolRecordID;
    }

    public int getInformationPointID()
    {
        return informationPointID;
    }

    public void setInformationPointID(int informationPointID)
    {
        this.informationPointID = informationPointID;
    }
}
