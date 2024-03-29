package com.example.patrolinspection.util;

import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.Log;

import com.example.patrolinspection.R;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

//用于向后端发送数据
public class HttpUtil
{
    //正式
    public static final String LocalAddress = "http://106.14.135.102:8887";

    //测试
//    public static final String LocalAddress = "http://47.101.68.214:8883";

    public static String getResourceURL(String url){
        return LocalAddress + "/resources/" + url;
    }

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
        RequestBody requestBody = RequestBody.create(jsonStr, JSON);
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
        Request request = new Request.Builder().url(address).addHeader("Authorization",credential).build();
        LogUtil.e("Login","111111111   " + request.header("Authorization"));
        client.newCall(request).enqueue(callback);
    }

    //心跳
    public static void heartbeatRequest(String address, String userID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("equipmentId", userID.split("-")[1])
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).put(requestBody).addHeader("Authorization",credential).build();
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
        map.put("longitude",longitude);
        map.put("latitude",latitude);
        map.put("height",height);
        map.put("floor",floor);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(jsonStr, JSON);
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

    //数据更新
    public static void updatingByEquipmentRequest(String address, String userID, String companyID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address + "?companyId=" + companyID + "&equipmentId=" + userID.split("-")[1]).addHeader("Authorization",credential).build();
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
        map.put("equipmentId",userID.split("-")[1]);
        map.put("policeId",policeID);
        map.put("startTime",""+startTime);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(jsonStr,JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //结束巡检
    public static void endPatrolRequest(String address, String userID, String companyID, String patrolRecordID, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        PatrolRecord patrolRecord = LitePal.where("internetID = ?",patrolRecordID).findFirst(PatrolRecord.class);
        List<PatrolPointRecord> patrolPointRecordList = LitePal.where("patrolRecordId = ?",patrolRecordID).order("time").find(PatrolPointRecord.class);
        patrolRecord.setPointPatrolRecords(patrolPointRecordList);
        String jsonStr = patrolRecord.toString();
        LogUtil.e("HttpUtil",jsonStr);
        RequestBody requestBody = RequestBody.create(jsonStr,JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //上传照片
    public static void fileRequest(String address, String userID, File file, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType fileType = MediaType.parse("image/jpeg");//数据类型为File格式，
        RequestBody fileBody = RequestBody.create(file, fileType);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
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
        map.put("equipmentId",userID.split("-")[1]);
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
        RequestBody requestBody = RequestBody.create(jsonStr,JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //处置事件
    public static void postHandleRecordRequest(String address, String userID, String eventRecordID, String policeID,
                                              String reportUnit, String detail, String disposalOperateType, String photo,long operateTime,
                                               okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("eventRecordId",eventRecordID);
        map.put("equipmentId",userID.split("-")[1]);
        map.put("policeId",policeID);
        map.put("disposalOperateType",disposalOperateType);
        map.put("reportUnit",reportUnit);
        map.put("photo",photo);
        map.put("detail",detail);
        map.put("operateime",""+operateTime);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(jsonStr, JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //人脸识别
    public static void faceRecognitionRequest(String address, String userID, String policeID, String faceType, File file, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();//创建OkHttpClient对象。
        MediaType fileType = MediaType.parse("image/jpeg");//数据类型为File格式，
        RequestBody fileBody = RequestBody.create(file, fileType);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .addFormDataPart("policeId",policeID)
                .addFormDataPart("faceType",faceType)
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).addHeader("Authorization",credential).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //新建签到
    public static void attendanceRequest(String address, String userID, String companyID, String policeID, String attendanceType, String signType, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("companyId",companyID);
        map.put("equipmentId",userID.split("-")[1]);
        map.put("policeId",policeID);
        map.put("attendanceType",attendanceType);
        map.put("signType",signType);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(jsonStr, JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //根据卡号查找保安
    public static void findPoliceByIcCardRequest(String address, String userID, String icCard, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address + "?cardNo=" + icCard).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //新建保安
    public static void postPoliceRequest(String address, String userID, String companyID, String name, String securityCard, String icCard,
                                         String identityCard, String birth, String sex, String nation, String tel, String duty, String photo, Callback callback){
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        HashMap<String, String> map = new HashMap<>();
        map.put("realName",name);
        map.put("companyId",companyID);
        map.put("photo",photo);
        map.put("securityCardNo",securityCard);
        map.put("icCardNo",icCard);
        map.put("identityNo",identityCard);
        map.put("birthday",birth);
        map.put("gender",sex);
        map.put("nation",nation);
        map.put("telephone",tel);
        map.put("mainDutyId",duty);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        RequestBody requestBody = RequestBody.create(jsonStr, JSON);
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //护校登录
    public static void schoolLoginRequest(String address, String userID, String policeID, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("equipmentId", userID.split("-")[1])
                .add("policeId",policeID)
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //护校登出
    public static void schoolLogoutRequest(String address, String userID, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("equipmentId", userID.split("-")[1])
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).post(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //获取护校事件列表
    public static void schoolEventRecordListRequest(String address, String userID, String schoolEventStatus, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address + "?equipmentId=" + userID.split("-")[1] + "&schoolEventStatus=" + schoolEventStatus).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }

    //处置护校事件
    public static void updateSchoolEventRecordRequest(String address, String userID, String policeID, String schoolEventRecordType, Callback callback){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("schoolEventRecordType", schoolEventRecordType)
                .add("policeId",policeID)
                .build();
        String credential = Credentials.basic(userID, "123456");
        Request request = new Request.Builder().url(address).put(requestBody).addHeader("Authorization",credential).build();
        client.newCall(request).enqueue(callback);
    }
}
