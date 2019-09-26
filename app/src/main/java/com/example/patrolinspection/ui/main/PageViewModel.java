package com.example.patrolinspection.ui.main;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

public class PageViewModel extends ViewModel
{

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>()
    {
        @Override
        public String apply(Integer input)
        {
            if(input == 1){
                return "已完成的事件列表";
            }else{
                return "未完成的事件列表";
            }

        }
    });

    public void setIndex(int index)
    {
        mIndex.setValue(index);
    }

    public LiveData<String> getText()
    {
        return mText;
    }
}