package com.example.patrolinspection.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import com.example.patrolinspection.util.LogUtil;

//开机自己启动的广播接收器
public class BootCompleteReceiver extends BroadcastReceiver
{
    public static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    private static final int START_TIME = 50;
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        String action = intent.getAction();
        String model = Build.MODEL;
        if (action == null){
            return;
        }
        LogUtil.e("BootCompleteReceiver",model);
        if(!model.equals("L2-H")){
            return;
        }
        if (action.equals(ACTION_BOOT)) {
            // 5秒后进行自启动
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startApp(context);
                }
            }, START_TIME);
        }
    }

    /**
     * 开启APP
     */
    private void startApp(Context context) {
        context.startActivity(context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName()));
    }
}
