package com.example.patrolinspection.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.patrolinspection.NoticeActivity;
import com.example.patrolinspection.adapter.NoticeAdapter;
import com.example.patrolinspection.db.Notice;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NoticePresenter
{
    private Context context;
    private SharedPreferences pref;

    public NoticePresenter(Context context, SharedPreferences pref){
        this.context = context;
        this.pref = pref;
    }

    public void updateNotice(final List<Notice> noticeList, final NoticeAdapter noticeAdapter){
        String userID = pref.getString("userID",null);
        String address = HttpUtil.LocalAddress + "/api/announce/list";
        HttpUtil.getHttp(address, userID, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                ((NoticeActivity)context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(context, "服务器连接错误", Toast
                                .LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responsData = response.body().string();
                LogUtil.e("Notice",responsData);
                LitePal.deleteAll(Notice.class);
                noticeList.clear();
                noticeList.addAll(Utility.handleNoticeList(responsData));
                LitePal.saveAll(noticeList);
                ((NoticeActivity)context).runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        noticeAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
