package com.example.patrolinspection.dagger2;

import com.example.patrolinspection.EventFoundActivity;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.InformationPoint;
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

import dagger.Component;

//dagger2框架 用于自动生成presenter
@Component(modules = {MyModule.class})
public interface MyComponent
{
    LoginPresenter loginPresenter();

    NoticePresenter noticePresenter();

    InformationPointPresenter informationPointPresenter();

    DataUpdatingPresenter dataUpdatingPresenter();

    FaceRecognitionPresenter faceRecognitionPresenter();

    EventFoundPresenter eventFoundPresenter();

    SignInOutPresenter signInOutPresenter();

    EventRecordPresenter eventRecordPresenter();

    PatrolingPresenter patrolingPresenter();

    PoliceRegisterPresenter policeRegisterPresenter();

    HandleRecordPresenter handleRecordPresenter();

    EventHandlePresenter eventHandlePresenter();

    SchoolEventPresenter schoolEventPresenter();

    SchoolEventHandlePresenter schoolEventHandlePresenter();
}
