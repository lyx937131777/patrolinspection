package com.example.patrolinspection.service;

public interface DownloadListener
{

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
