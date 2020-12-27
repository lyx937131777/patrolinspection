package com.example.patrolinspection.presenter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

//保安注册
public class PoliceRegisterPresenter
{
    private Context context;
    private SharedPreferences pref;

    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    public PoliceRegisterPresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    //保安注册（上传图片）
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
//        progressBar = new ProgressBar(context);
//        progressBar.setVisibility(View.VISIBLE);
        progressDialog = ProgressDialog.show(context,"","上传中...");
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
//                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    final String responseData = response.body().string();
                    LogUtil.e("PoliceRegisterPresenter",responseData);
                    String photoPath = Utility.checkString(responseData,"msg");
                    register(name,securityCard,icCard,identityCard,birth,sex,nation,tel,duty,photoPath);
                }
            });
        }else{
            register(name,securityCard,icCard,identityCard,birth,sex,nation,tel,duty,"");
        }
    }

    //注册保安
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
//                        progressBar.setVisibility(View.GONE);
                    }
                });
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseData = response.body().string();
                LogUtil.e("PoliceRegisterPresenter",responseData);
                if(Utility.checkString(responseData,"code").equals("500")){
                    ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, Utility.checkString(responseData,"msg"), Toast
                                    .LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else{
                    Police police = Utility.handlePolice(responseData);
                    police.save();
                    ((PoliceRegisterActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "注册成功", Toast
                                    .LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    ((PoliceRegisterActivity) context).finish();
                }
                progressDialog.dismiss();
            }
        });
    }
}
