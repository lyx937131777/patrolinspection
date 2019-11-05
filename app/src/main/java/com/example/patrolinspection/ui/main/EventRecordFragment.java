package com.example.patrolinspection.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import com.example.patrolinspection.R;
import com.example.patrolinspection.adapter.EventRecordAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.presenter.EventRecordPresenter;
import com.example.patrolinspection.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventRecordFragment extends Fragment
{

    private static final String ARG_SECTION_NUMBER = "section_number";

//    private PageViewModel pageViewModel;

    private int index;
    private List<EventRecord> eventRecordList = new ArrayList<>();
    private EventRecordAdapter adapter;
    private EventRecordPresenter eventRecordPresenter;

    public static EventRecordFragment newInstance(int index)
    {
        EventRecordFragment fragment = new EventRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        index = 1;
        if (getArguments() != null)
        {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_event_record, container, false);
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(getContext())).build();
        eventRecordPresenter = myComponent.eventRecordPresenter();

        initEventRecord();
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EventRecordAdapter(eventRecordList,getType());
        recyclerView.setAdapter(adapter);
        LogUtil.e("EventRecordFragment","onCreateView");
//        eventRecordPresenter.updateRecord(eventRecordList,adapter,getType());
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LogUtil.e("EventRecordFragment","onStart");
        eventRecordPresenter.updateRecord(eventRecordList,adapter,getType());
    }

    private String getType()
    {
        if(index == 1){
            return "ended";
        }else{
            return "handling";
        }
    }

    private void initEventRecord()
    {
        eventRecordList.clear();
    }
}