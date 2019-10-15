package com.example.patrolinspection.util;

import android.text.TextUtils;
import android.util.Base64;

import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.Notice;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.Police;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    //返回Json数据的userID值
    public static String getUserID(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray dataArray = jsonObject.getJSONArray("datas");
            JSONObject dataObject = dataArray.getJSONObject(0);
            String userID = dataObject.getString("id");
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

    //更新线路列表
    public static List<PatrolLine> handlePatrolLineList(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray dataArray = jsonObject.getJSONArray("datas");
                JSONObject dataObject = dataArray.getJSONObject(0);
                JSONArray jsonArray = dataObject.getJSONArray("content");
                String patrolLineJson = jsonArray.toString();
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

    //后台给的日期格式 转化为日期Date
    public static Date stringToDate(String s){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
//        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
//        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //后台给的日期格式 转化为目标的日期格式字符串
    public static String dateStringToString(String s, String formatString){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
        DateFormat df2 = new SimpleDateFormat(formatString);
        df2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date);
    }

    //时分秒转化成时分的格式
    public static String hmsToHm(String s){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df2.format(date);
    }

    /**
     * 将图片转换成Base64编码的字符串
     * @param path
     * @return base64编码的字符串
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        LogUtil.e("Push:base64",result.length()+"      "+ result);
        return result;
    }
}
