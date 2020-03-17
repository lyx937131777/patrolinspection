package com.example.patrolinspection.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.example.patrolinspection.MainActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SystemParameterActivity;
import com.example.patrolinspection.util.LogUtil;

import java.io.File;

public class DownloadService extends Service
{

    private DownloadTask downloadTask;

    private String downloadUrl;

    private DownloadListener listener = new DownloadListener()
    {
        @Override
        public void onProgress(int progress)
        {
            getNotificationManager().notify(1, getNotification("下载中...", progress));
        }

        @Override
        public void onSuccess()
        {
            downloadTask = null;
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功", -1));
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + fileName);
            if(file.exists()){
                installAPK(file);
            }

        }

        @Override
        public void onFailed()
        {
            downloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败", -1));
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused()
        {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "已暂停", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled()
        {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "已取消", Toast.LENGTH_SHORT).show();
        }

    };

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public class DownloadBinder extends Binder
    {

        public void startDownload(String url)
        {
            if (downloadTask == null)
            {
                downloadUrl = url;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("下载中...", 0));
                Toast.makeText(DownloadService.this, "下载中...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload()
        {
            if (downloadTask != null)
            {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload()
        {
            if (downloadTask != null)
            {
                downloadTask.cancelDownload();
            } else
            {
                if (downloadUrl != null)
                {
                    // 取消下载时需将文件删除，并将通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists())
                    {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "已取消", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private NotificationManager getNotificationManager()
    {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress)
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"default");
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0)
        {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    //调用系统的安装方法
    private void installAPK(File savedFile) {
        //调用系统的安装方法
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            String authority = getApplicationContext().getPackageName() + ".fileProvider";
            data = FileProvider.getUriForFile(this, authority, savedFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            LogUtil.e("AutoUpdate","7.0data="+data);
        } else {
            data = Uri.fromFile(savedFile);
            LogUtil.e("AutoUpdate","111data="+data);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
        //弹出安装窗口把原程序关闭。
        //避免安装完毕点击打开时没反应
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
