package com.example.patrolinspection.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import com.example.patrolinspection.db.Company;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.HandleRecord;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.Notice;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.db.SchoolEventRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

//用于解析后端返回的数据
public class Utility
{
    //返回Json数据的companyID值
    public static String getCompanyID(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("datas");
            JSONObject dataObject = dataArray.getJSONObject(0);
            String companyID = dataObject.getString("companyId");
            return companyID;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "000";
    }

    //返回Json数据的设备类型
    public static String getEquipmentType(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("datas");
            JSONObject dataObject = dataArray.getJSONObject(0);
            String equipmentType = dataObject.getString("equipmentType");
            return equipmentType;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "000";
    }

    //返回Json数据的company类
    public static Company getCompany(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("datas");
            JSONObject dataObject = dataArray.getJSONObject(0);
            JSONObject companyObject = dataObject.getJSONObject("company");
            String jsonString = companyObject.toString();
            return  new Gson().fromJson(jsonString, Company.class);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //返回Json数据的userID值
    public static String getUserID(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("datas");
            JSONObject dataObject = dataArray.getJSONObject(0);
            String userID = "phone-"+dataObject.getString("id");
            return  userID;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "000";
    }

    //返回Json数据的特定string值
    public static String checkString(String response, String string)
    {
        try
        {
            JSONObject dataObject = new JSONObject(response);
            return dataObject.getString(string);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "000";
    }

    //获得Patrolrecord
    public static PatrolRecord handlePatrolRecord(String response){
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                String jsonString = dataObject.toString();
                return  new Gson().fromJson(jsonString, PatrolRecord.class);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //心跳获得更新列表
    public static List<String> handleUpdateList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("updateList");
                String noticeJson = jsonArray.toString();
                return new Gson().fromJson(noticeJson, new TypeToken<List<String>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //心跳获得相应布尔值
    public static boolean checkHeartbeatBoolean(String response, String s)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                return dataObject.getBoolean(s);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    //心跳获得相应string字段
    public static String checkHeartbeatString(String response, String s)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                return dataObject.getString(s);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    //获得Police
    public static Police handlePolice(String response){
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                String jsonString = dataObject.toString();
                return  new Gson().fromJson(jsonString, Police.class);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //处理公告
    public static List<Notice> handleNoticeList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String noticeJson = jsonArray.toString();
                return new Gson().fromJson(noticeJson, new TypeToken<List<Notice>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新事件列表
    public static List<Event> handleEventList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String eventJson = jsonArray.toString();
                return new Gson().fromJson(eventJson, new TypeToken<List<Event>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新线路列表 TODO 这里和其他不一样
    public static List<PatrolLine> handlePatrolLineList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                String patrolLineJson = dataArray.toString();
                return new Gson().fromJson(patrolLineJson, new TypeToken<List<PatrolLine>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新计划列表
    public static List<PatrolPlan> handlePatrolPlanList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String patrolPlanJson = jsonArray.toString();
                return new Gson().fromJson(patrolPlanJson, new TypeToken<List<PatrolPlan>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新计划列表（排班）
    public static List<PatrolSchedule> handlePatrolScheduleList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String patrolScheduleJson = jsonArray.toString();
                return new Gson().fromJson(patrolScheduleJson, new TypeToken<List<PatrolSchedule>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新线路列表（更新信息点）
    public static List<InformationPoint> handleInformationPointList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String informationPointJson = jsonArray.toString();
                return new Gson().fromJson(informationPointJson, new TypeToken<List<InformationPoint>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新保安列表
    public static List<Police> handlePoliceList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String policeJson = jsonArray.toString();
                return new Gson().fromJson(policeJson, new TypeToken<List<Police>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }


    //更新事件记录列表
    public static List<EventRecord> handleEventRecordList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String eventRecordJson = jsonArray.toString();
                return new Gson().fromJson(eventRecordJson, new TypeToken<List<EventRecord>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新护校事件记录列表
    public static List<SchoolEventRecord> handleSchoolEventRecordList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                String schoolEventRecordJson = dataArray.toString();
                return new Gson().fromJson(schoolEventRecordJson, new TypeToken<List<SchoolEventRecord>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    //更新处置记录列表
    public static List<HandleRecord> handleHandleRecordList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("disposalRecordInfos");
                String handleRecordJson = jsonArray.toString();
                return new Gson().fromJson(handleRecordJson, new TypeToken<List<HandleRecord>>() {}.getType());
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

}
