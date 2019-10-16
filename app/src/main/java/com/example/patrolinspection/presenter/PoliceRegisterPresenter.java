package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.PoliceRegisterActivity;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PoliceRegisterPresenter
{
    private Context context;
    private SharedPreferences pref;

    public PoliceRegisterPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void register(final String name, final String securityCard, final String icCard, final String identityCard, final String birth,
                         final String sex, final String nation, final String tel, final String duty, File photo){
        if(tel == null || tel.length() == 0){
            Toast.makeText(context,"手机号不得为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(tel.length() != 11){
            Toast.makeText(context,"请输入正确的手机号",Toast.LENGTH_LONG).show();
            return;
        }
        if(photo != null){
            String address = HttpUtil.LocalAddress + "/api/file";
            String userID = pref.getString("userID",null);
            HttpUtil.fileRequest(address, userID, photo, new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    e.printStackTrace();
                    ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
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
                    final String responsData = response.body().string();
                    LogUtil.e("PoliceRegisterPresenter",responsData);
                    String photoPath = Utility.checkString(responsData,"msg");
                    register(name,securityCard,icCard,identityCard,birth,sex,nation,tel,duty,photoPath);
                }
            });
        }else{
            register(name,securityCard,icCard,identityCard,birth,sex,nation,tel,duty,"");
        }
    }

    private void register(String name, String securityCard, String icCard, String identityCard, String birth,
                          String sex, String nation, String tel, String duty, final String photo){
        String address = HttpUtil.LocalAddress + "/api/police/app";
        String userID = pref.getString("userID",null);
        String companyID = pref.getString("companyID",null);
        HttpUtil.postPoliceRequest(address, userID, companyID, name, securityCard, icCard, identityCard, birth, sex, nation, tel, duty, photo, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
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
                final String responsData = response.body().string();
                LogUtil.e("PoliceRegisterPresenter",responsData);
                if(Utility.checkString(responsData,"code").equals("500")){
                    ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responsData,"msg"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }else{
                    Police police = Utility.handlePolice(responsData);
                    police.save();
                    ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responsData,"注册成功"), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                    ((PoliceRegisterActivity) context).finish();
                }

            }
        });
    }
}
