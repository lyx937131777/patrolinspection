package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.patrolinspection.adapter.InformationPointAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.presenter.PatrolingPresenter;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatrolingActivity extends AppCompatActivity
{
    private List<PatrolPointRecord> patrolPointRecordList = new ArrayList<>();
    private InformationPointAdapter adapter;

    private TextView ipCount;
    private Button eventFound;
    private Button eventHandle;
    private Button endPatrol;

    private String recordID;
    private PatrolRecord patrolRecord;
    private PatrolSchedule patrolSchedule;
    private String lineID;
    private PatrolLine patrolLine;

    private int countAll;
    private int countPatrolled;

    private PatrolingPresenter patrolingPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patroling);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        patrolingPresenter = myComponent.patrolingPresenter();

        Intent intent = getIntent();
        recordID = intent.getStringExtra("record");
        patrolRecord = LitePal.where("internetID = ?",recordID).findFirst(PatrolRecord.class);
        patrolSchedule = LitePal.where("internetID = ?",patrolRecord.getPatrolScheduleId()).findFirst(PatrolSchedule.class);
        lineID = patrolSchedule.getPatrolLineId();
        patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
        actionBar.setTitle(patrolLine.getPatrolLineName());


        final String startTime =patrolSchedule.getStartTime();
        String endTime = patrolSchedule.getEndTime();
        int duringTime = 60 ;//待修改
        int limit = Integer.valueOf(patrolSchedule.getErrorRange());
        Date date = new Time(patrolRecord.getStartTimeLong());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String realStartTime =  format.format(calendar.getTime());
        calendar.add(Calendar.MINUTE,duringTime-limit);
        String realEndTime = format.format(calendar.getTime());
        calendar.add(Calendar.MINUTE,limit*2);
        String realEndTime2 = format.format(calendar.getTime());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("计划时间： " + startTime + " - " + endTime + " 共计" + duringTime + "分钟\n");
        stringBuilder.append("实际开始时间： " + realStartTime + "\n规定结束时间：" + realEndTime + " - " + realEndTime2);

        TextView lineInformation = findViewById(R.id.line_information);
        lineInformation.setText(stringBuilder.toString());

        ipCount = findViewById(R.id.ip_count);
        initIP();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationPointAdapter(patrolPointRecordList);
        recyclerView.setAdapter(adapter);

        eventFound = findViewById(R.id.event_found);
        eventHandle = findViewById(R.id.event_handle);
        endPatrol = findViewById(R.id.end_patrol);
        eventFound.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent2 = new Intent(PatrolingActivity.this, EventFoundActivity.class);
                intent2.putExtra("type","patrol");
                intent2.putExtra("line",lineID);
                startActivity(intent2);
            }
        });
        eventHandle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        endPatrol.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                patrolingPresenter.endPatrol(recordID);
            }
        });
    }

    private void initIP()
    {
        patrolPointRecordList.clear();
        List<PatrolIP> patrolIPList = LitePal.where("patrolLineID = ?",lineID).find(PatrolIP.class);
        for(PatrolIP patrolIP : patrolIPList){
            PatrolPointRecord patrolPointRecord = new PatrolPointRecord(recordID,patrolIP);
            patrolPointRecordList.add(patrolPointRecord);
            patrolPointRecord.save();
        }
        countAll = patrolPointRecordList.size();
        countPatrolled = 0;
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

    public void addCount(){
        countPatrolled++;
        ipCount.setText(countPatrolled + "/" + countAll);
    }
}
