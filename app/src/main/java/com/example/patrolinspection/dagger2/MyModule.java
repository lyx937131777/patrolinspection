package com.example.patrolinspection.dagger2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.patrolinspection.presenter.LoginPresenter;
import com.example.patrolinspection.presenter.NoticePresenter;
import com.example.patrolinspection.util.CheckUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class MyModule
{
    private Context context;

    public MyModule(Context context)
    {
        this.context = context;
    }

    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public CheckUtil provideCheckUtil(Context context)
    {
        return new CheckUtil(context);
    }

    @Provides
    public Context provideContext()
    {
        return context;
    }

    @Provides
    public LoginPresenter provideLoginPresenter(Context context, SharedPreferences pref, CheckUtil checkUtil)
    {
        return new LoginPresenter(context,pref,checkUtil);
    }

    @Provides
    public NoticePresenter provideNoticePresenter(Context context, SharedPreferences pref){
        return  new NoticePresenter(context, pref);
    }
}
