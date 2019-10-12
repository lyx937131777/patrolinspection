package com.example.patrolinspection.util;

import android.content.SharedPreferences;

import com.example.patrolinspection.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.http2.Header;

public class HttpUtil
{
    public static final String LocalAddress = "http://47.104.70.81:8887";


    //登陆界面
    public static void loginRequest(String address, String physicalNo,
                                    okhttp3.Callback callback)
    {
        LogUtil.e("HttpUtil", address);
        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("physicalNo", physicalNo)
//                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonStr = "{\"physicalNo\":\"" + physicalNo + "\"}";//json数据.
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
//        Request request = new Request.Builder().url("http://wwww.baidu.com").build();
        client.newCall(request).enqueue(callback);
        LogUtil.e("Login",address);
    }

    //get 登录界面
    public static void getHttp(String address,String userID, okhttp3.Callback callback)
    {
//        OkHttpClient client = buildBasicAuthClient(userID,"123456");
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address)
                .addHeader("Authorization",credential)
                .build();
        LogUtil.e("Login","111111111   " + request.header("Authorization"));
        client.newCall(request).enqueue(callback);
    }

    //注册信息点
    public static void registerIPRequest(String address,String userID, String companyID, String id, String name, String longitude,
                                         String latitude, String height, String floor, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        map.put("pointNo",id);
        map.put("pointName",name);
        map.put("longitude",latitude);
        map.put("latitude",latitude);
        map.put("height",height);
        map.put("floor",floor);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //数据更新
    public static void updatingRequest(String address, String userID, String companyID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address + "?companyId=" + companyID).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
        LogUtil.e("DataUpdating","发送成功");
    }

    //开始巡检
    public static void startPatrolRequset(String address, String userID, String companyID, String policeID, long startTime,String scheduleID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        map.put("patrolScheduleId",scheduleID);
        map.put("equipmentId",userID);
        map.put("policeId",policeID);
        map.put("startTime",""+startTime);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }
    //结束巡检
    public static void endPatrolRequest(String address, String userID, String companyID, String patrolRecordID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).put(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //上传照片
    public static void fileRequest(String address, String userID, File file, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType fileType = MediaType.parse("image/png");//数据类型为File格式，
        RequestBody fileBody = RequestBody.create(fileType , file );
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "1.jpg", fileBody)
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).addHeader("Authorization",credential).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //发布事件
    public static void postEventRecordRequest(String address, String userID, String companyID, String eventID, String policeID,
                                              String reportUnit, String detail, String disposalOperateType, String photo,long operateTime,
                                              String patrolRecordID, String pointID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        map.put("eventId",eventID);
        map.put("equipmentId",userID);
        map.put("policeId",policeID);
        map.put("disposalOperateType",disposalOperateType);
        map.put("reportUnit",reportUnit);
        map.put("photo",photo);
        map.put("detail",detail);
        map.put("operateime",""+operateTime);
        map.put("patrolRecordId",patrolRecordID);
        map.put("pointId",pointID);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        jsonStr = "["+jsonStr+"]";
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //人脸识别
    public static void faceRecognitionRequest(String address, String userID, String policeID, String faceType, File file, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType fileType = MediaType.parse("image/jpeg");//数据类型为File格式，
        RequestBody fileBody = RequestBody.create(fileType , file );
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "1.jpeg", fileBody)
                .addFormDataPart("policeId",policeID)
                .addFormDataPart("faceType",faceType)
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).addHeader("Authorization",credential).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //新建前端
    public static void attendanceRequest(String address, String userID, String companyID, String policeID, String attendanceType, String signType, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        map.put("equipmentId",userID);
        map.put("policeId",policeID);
        map.put("attendanceType",attendanceType);
        map.put("signType",signType);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //发送巡检
}
