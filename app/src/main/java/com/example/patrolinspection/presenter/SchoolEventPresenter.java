package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.SchoolEventActivity;
import com.example.patrolinspection.adapter.SchoolEventRecordAdapter;
import com.example.patrolinspection.db.SchoolEventRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//护校事件
public class SchoolEventPresenter
{
    private Context context;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    public SchoolEventPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    //登出
    public void logout(){
        progressDialog = ProgressDialog.show(context,"","登出中...");

        String address = HttpUtil.LocalAddress + "/api/equipment/school_logout";
        String userID = pref.getString("userID",null);
        HttpUtil.schoolLogoutRequest(address, userID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((SchoolEventActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("SchoolEventPresenter",responseData);
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((SchoolEventActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }else{
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("schoolPolice","null");
                    editor.apply();
                    ((SchoolEventActivity)context).finish();
                }
                progressDialog.dismiss();
            }
        });
    }

    //获取护校事件记录的列表
    public void updateRecord(final List<SchoolEventRecord> schoolEventRecordList, final SchoolEventRecordAdapter adapter, final String type){
        String address = HttpUtil.LocalAddress + "/api/schoolRecord/app_list";
        String userID = pref.getString("userID",null);
        HttpUtil.schoolEventRecordListRequest(address, userID, type, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((SchoolEventActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("SchoolEventPresenter",responseData);
                schoolEventRecordList.clear();
                schoolEventRecordList.addAll(Utility.handleSchoolEventRecordList(responseData));
                //TODO 排序
                ((SchoolEventActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
