package com.example.patrolinspection.dagger2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.presenter.EventFoundPresenter;
import com.example.patrolinspection.presenter.EventHandlePresenter;
import com.example.patrolinspection.presenter.EventRecordPresenter;
import com.example.patrolinspection.presenter.FaceRecognitionPresenter;
import com.example.patrolinspection.presenter.HandleRecordPresenter;
import com.example.patrolinspection.presenter.InformationPointPresenter;
import com.example.patrolinspection.presenter.LoginPresenter;
import com.example.patrolinspection.presenter.NoticePresenter;
import com.example.patrolinspection.presenter.PatrolingPresenter;
import com.example.patrolinspection.presenter.PoliceRegisterPresenter;
import com.example.patrolinspection.presenter.SchoolEventHandlePresenter;
import com.example.patrolinspection.presenter.SchoolEventPresenter;
import com.example.patrolinspection.presenter.SignInOutPresenter;
import com.example.patrolinspection.util.CheckUtil;

import dagger.Module;
import dagger.Provides;

//dagger2框架 用于自动生成presenter
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
    //TODO checkutil

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

    @Provides
    public InformationPointPresenter provideInformationPointPresenter(Context context, SharedPreferences pref){
        return new InformationPointPresenter(context, pref);
    }

    @Provides
    public DataUpdatingPresenter provideDataUpdatingPresenter(Context context, SharedPreferences pref){
        return new DataUpdatingPresenter(context, pref);
    }

    @Provides
    public FaceRecognitionPresenter provideFaceRecognitionPresenter(Context context, SharedPreferences pref){
        return new FaceRecognitionPresenter(context,pref);
    }

    @Provides
    public EventFoundPresenter provideEventFoundPresenter(Context context, SharedPreferences pref){
        return new EventFoundPresenter(context,pref);
    }

    @Provides
    public SignInOutPresenter provideSignInOutPresenter(Context context, SharedPreferences pref){
        return new SignInOutPresenter(context,pref);
    }

    @Provides
    public EventRecordPresenter provideEventRecordPresenter(Context context, SharedPreferences pref){
        return new EventRecordPresenter(context,pref);
    }

    @Provides
    public PatrolingPresenter providePatrolingPresenter(Context context, SharedPreferences pref){
        return new PatrolingPresenter(context,pref);
    }

    @Provides
    public PoliceRegisterPresenter providePoliceRegisterPresenter(Context context, SharedPreferences pref){
        return  new PoliceRegisterPresenter(context, pref);
    }

    @Provides
    public HandleRecordPresenter provideHandleRecordPresenter(Context context, SharedPreferences pref){
        return new HandleRecordPresenter(context, pref);
    }

    @Provides
    public EventHandlePresenter provideEventHandlePresenter(Context context, SharedPreferences pref){
        return new EventHandlePresenter(context,pref);
    }

    @Provides
    public SchoolEventPresenter provideSchoolEventPresenter(Context context, SharedPreferences pref){
        return new SchoolEventPresenter(context,pref);
    }

    @Provides
    public SchoolEventHandlePresenter provideSchoolEventHandlePresenter(Context context, SharedPreferences pref){
        return new SchoolEventHandlePresenter(context,pref);
    }
}
