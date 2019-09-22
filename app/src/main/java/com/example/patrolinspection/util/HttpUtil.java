package com.example.patrolinspection.util;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil
{
    public static final String LocalAddress = "http://129.204.74.128:8080/timeline";

    //登陆界面
    public static void loginRequest(String address, String userID, String password,
                                    okhttp3.Callback callback)
    {
        LogUtil.e("HttpUtil", address);
        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("userID", userID)
//                .add("password", password)
//                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
        String jsonStr = "{\"userID\":\"" + userID + "\",\"password\":\"" + password + "\"}";//json数据.
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //注册界面
    public static void registerRequest(String address, String userID, String password, String
            nickname, okhttp3.Callback callback)
    {
        LogUtil.e("HttpUtil", address);
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        String jsonStr = "{\"userID\":\"" + userID + "\",\"password\":\"" + password + "\"," +
                "\"nickname\":\"" + nickname + "\"}";//json数据.
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //refresh
    public static void refreshRequest(String address, int articleID, int show, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        String jsonStr = "{\"articleID\":\""+articleID+"\",\"show\":\""+show+"\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //more
    public static void moreRequest(String address, int articleID, int show, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        String jsonStr = "{\"articleID\":\""+articleID+"\",\"show\":\""+show+"\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    // push
    public static void pushRequest(String address, String userID, String content, String image, okhttp3.Callback callback)
    {

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        String jsonStr = "{\"userID\":\""+userID+"\",\"content\":\""+content+"\",\"image\":\""+image+"\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        LogUtil.e("Http",jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    //delete
    public static void deleteRequest(String address, int articleID, okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式
        String jsonStr = "{\"articleID\":\""+articleID+"\"}";
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
