package com.example.patrolinspection.util;

import android.text.TextUtils;
import android.util.Base64;

import com.example.patrolinspection.db.Notice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    //返回Json数据的message值
    public static String checkMessage(String response)
    {
        try
        {
            JSONObject dataObject = new JSONObject(response);
            return dataObject.getString("message");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return "000";
    }

    //返回Json数据的errorType值
    public static String checkErrorType(String response)
    {
        try
        {
            JSONObject dataObject = new JSONObject(response);
            return dataObject.getString("errorType");
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

    //refresh more
    public static List<Notice> handlNoticeList(String response)
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
