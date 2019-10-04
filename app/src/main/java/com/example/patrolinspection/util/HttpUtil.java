package com.example.patrolinspection.util;

import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.http2.Header;

public class HttpUtil
{
    public static final String LocalAddress = "http://47.101.148.57:8887";

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

    private static OkHttpClient buildBasicAuthClient(final String name, final String password) {
        return new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException
            {
                String credential = Credentials.basic(name, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
    }
}
