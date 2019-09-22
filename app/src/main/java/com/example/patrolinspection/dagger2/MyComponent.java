package com.example.patrolinspection.dagger2;

import com.example.patrolinspection.presenter.LoginPresenter;

import dagger.Component;

@Component(modules = {MyModule.class})
public interface MyComponent
{
    LoginPresenter loginPresenter();
}
