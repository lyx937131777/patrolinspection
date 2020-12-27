package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.patrolinspection.adapter.InformationPointAdapter;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//信息点记录界面
public class PointRecordListActivity extends AppCompatActivity
{

    //recycleView
    private List<PatrolPointRecord> patrolPointRecordList = new ArrayList<>();
    private InformationPointAdapter adapter;
    private PatrolPointRecord tempPointRecord;

    //View控件
    private TextView ipCount;
    private TextView lineInformation;

    //参数
    private String recordID;
    private PatrolRecord patrolRecord;

    //计数
    private int countAll;
    private int countPatrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_record_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //获取参数
        Intent intent = getIntent();
        recordID = intent.getStringExtra("record");
        patrolRecord = LitePal.where("internetID = ?",recordID).findFirst(PatrolRecord.class);
        actionBar.setTitle(patrolRecord.getLineName());

        //显示信息
        lineInformation = findViewById(R.id.line_information);
        StringBuilder stringBuilder = new StringBuilder();
        Date startTime = new Date(patrolRecord.getStartTimeLong());
        Date endTime = new Date(patrolRecord.getEndTime());
        stringBuilder.append("开始时间： " + TimeUtil.dateToString(startTime,"yyyy-MM-dd HH:mm") + "\n");
        stringBuilder.append("结束时间： " + TimeUtil.dateToString(endTime,"yyyy-MM-dd HH:mm") + "\n");
        stringBuilder.append("线路名称： " + patrolRecord.getLineName());
        lineInformation.setText(stringBuilder.toString());

        //recycleView
        ipCount = findViewById(R.id.ip_count);
        initIP();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationPointAdapter(patrolPointRecordList);
        recyclerView.setAdapter(adapter);
    }

    private void initIP()
    {
        patrolPointRecordList.clear();
        patrolPointRecordList.addAll(LitePal.where("patrolRecordId = ?",recordID).find(PatrolPointRecord.class));

        countAll = patrolPointRecordList.size();
        countPatrolled = 0;
        for(PatrolPointRecord patrolPointRecord : patrolPointRecordList){
            if(patrolPointRecord.getTime() != 0){
                countPatrolled++;
            }
        }
        ipCount.setText(countPatrolled + "/" + countAll);
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
