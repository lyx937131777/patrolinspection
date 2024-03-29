package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.EventRecordActivity;
import com.example.patrolinspection.adapter.EventRecordAdapter;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//事件记录
public class EventRecordPresenter
{
    private Context context;
    private SharedPreferences pref;

    public EventRecordPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    //获取事件记录列表
    public void updateRecord(final List<EventRecord> eventRecordList, final EventRecordAdapter adapter, final String type){
        String address = HttpUtil.LocalAddress + "/api/eventRecord/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.updatingRequest(address, userID, companyID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((EventRecordActivity)context).runOnUiThread(new Runnable() {
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
                LogUtil.e("EventRecordPresenter",responseData);
                eventRecordList.clear();
                List<EventRecord> tempList = Utility.handleEventRecordList(responseData);
                for(EventRecord eventRecord : tempList){
                    if(type.equals("ended")){
                        if(eventRecord.getDisposalOperateType().equals("end")){
                            eventRecordList.add(eventRecord);
                        }
                    }else{
                        if(!eventRecord.getDisposalOperateType().equals("end")){
                            eventRecordList.add(eventRecord);
                        }
                    }
                }
                ((EventRecordActivity)context).runOnUiThread(new Runnable() {
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
