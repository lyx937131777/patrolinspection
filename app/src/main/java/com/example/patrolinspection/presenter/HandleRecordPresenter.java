package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.EventRecordActivity;
import com.example.patrolinspection.HandleRecordActivity;
import com.example.patrolinspection.adapter.HandleRecordAdapter;
import com.example.patrolinspection.db.HandleRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HandleRecordPresenter
{
    private Context context;
    private SharedPreferences pref;

    public HandleRecordPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void updateRecord(final List<HandleRecord> handleRecordList, final HandleRecordAdapter adapter, String eventRecordID){
        String address = HttpUtil.LocalAddress + "/api/eventRecord/"+eventRecordID;
        String userID = pref.getString("userID",null);
        HttpUtil.getHttp(address, userID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((HandleRecordActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "服务器连接错误", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("HandleRecordPresenter",responseData);
                handleRecordList.clear();
                handleRecordList.addAll(Utility.handleHandleRecordList(responseData));
                ((HandleRecordActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                //TODO 本地保存
            }
        });
    }

}
