package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.patrolinspection.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PatrolingPresenter
{
    private Context context;
    private SharedPreferences pref;

    public PatrolingPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void endPatrol(String patrolRecordID){
        String address = HttpUtil.LocalAddress + "/api/schedule/list";
        String companyID = pref.getString("companyID",null);
        String userID = pref.getString("userID",null);
        HttpUtil.endPatrolRequest(address, userID, companyID, patrolRecordID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {

            }
        });
    }
}
