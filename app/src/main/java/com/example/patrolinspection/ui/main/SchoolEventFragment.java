package com.example.patrolinspection.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.patrolinspection.R;
import com.example.patrolinspection.adapter.EventRecordAdapter;
import com.example.patrolinspection.adapter.SchoolEventRecordAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.SchoolEventRecord;
import com.example.patrolinspection.presenter.EventRecordPresenter;
import com.example.patrolinspection.presenter.SchoolEventPresenter;
import com.example.patrolinspection.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SchoolEventFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";

//    private PageViewModel pageViewModel;

    private int index;
    private List<SchoolEventRecord> schoolEventRecordList = new ArrayList<>();
    private SchoolEventRecordAdapter adapter;
    private SchoolEventPresenter schoolEventPresenter;

    public static SchoolEventFragment newInstance(int index)
    {
        SchoolEventFragment fragment = new SchoolEventFragment();
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
        View root = inflater.inflate(R.layout.fragment_school_event, container, false);
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(getContext())).build();
        schoolEventPresenter = myComponent.schoolEventPresenter();

        initSchoolEventRecord();
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SchoolEventRecordAdapter(schoolEventRecordList,getType());
        recyclerView.setAdapter(adapter);
//        eventRecordPresenter.updateRecord(eventRecordList,adapter,getType());
        return root;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        LogUtil.e("EventRecordFragment","onStart");
        schoolEventPresenter.updateRecord(schoolEventRecordList,adapter,getType());
    }

    private String getType()
    {
        if(index == 1){
            return "undispose";
        }else if(index == 2){
            return "disposing";
        }else {
            return "disposed";
        }
    }

    private void initSchoolEventRecord()
    {
        schoolEventRecordList.clear();
    }
}
