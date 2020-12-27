package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.patrolinspection.adapter.EventRecordListAdapter;
import com.example.patrolinspection.adapter.UploadListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//数据更新界面的事件记录列表界面
public class EventRecordListActivity extends AppCompatActivity
{
    private List<EventRecord> eventRecordList = new ArrayList<>();
    private EventRecordListAdapter adapter;

    private Button clearButton;
    private Button uploadButton;

    private DataUpdatingPresenter dataUpdatingPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_record_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        initEventRecord();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EventRecordListAdapter(eventRecordList);
        recyclerView.setAdapter(adapter);

        uploadButton = findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                List<EventRecord> eventRecordList = LitePal.where("upload = ?","0").find(EventRecord.class);
                if(eventRecordList.size() == 0){
                    Toast.makeText(EventRecordListActivity.this, "所有记录均已上传",Toast.LENGTH_LONG).show();
                }else{
                    dataUpdatingPresenter.uploadEventRecord();
                }
            }
        });

        clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LitePal.deleteAll(EventRecord.class,"upload = ?","1");
                refresh();
                Toast.makeText(EventRecordListActivity.this, "已清除所有已上传的记录",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initEventRecord()
    {
        eventRecordList.clear();
        eventRecordList.addAll(LitePal.findAll(EventRecord.class));
        Collections.sort(eventRecordList, new Comparator<EventRecord>()
        {
            @Override
            public int compare(EventRecord o1, EventRecord o2)
            {
                //若为正o1在后
                if((o1.isUpload() && o2.isUpload()) || (!o1.isUpload() && !o2.isUpload())){
                    return (int)(o2.getTime() - o1.getTime());
                }else if(o1.isUpload()){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
    }

    public void refresh()
    {
        initEventRecord();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
