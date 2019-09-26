package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.patrolinspection.adapter.InformationPointAdapter;
import com.example.patrolinspection.adapter.PatrolInspectionAdapter;
import com.example.patrolinspection.adapter.PatrolLineAdapter;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolInspection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PatrolingActivity extends AppCompatActivity
{
    private InformationPoint[] informationPoints = {new InformationPoint(1,"ABC","正门"),new InformationPoint(2,"ABC","食堂"),
            new InformationPoint(3,"ABC","图书馆"),new InformationPoint(4,"ABC","理科楼"),new InformationPoint(5,"ABC","毛像"),
            new InformationPoint(6,"ABC","操场操场操场操场"),new InformationPoint(7,"AC","物理楼科技楼地理楼信息楼"),new InformationPoint(8,"ABC","后门")};
    private List<InformationPoint> informationPointList = new ArrayList<>();
    private InformationPointAdapter adapter;
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

        Intent intent = getIntent();
        String title = intent.getStringExtra("line");
        actionBar.setTitle(title);


        String startTime = "10:00";
        String endTime = "11:00";
        int duringTime = 60 ;
        int limit = 15;
        Calendar calendar = Calendar.getInstance();
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

        initIP();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InformationPointAdapter(informationPointList);
        recyclerView.setAdapter(adapter);
    }

    private void initIP()
    {
        informationPointList.clear();
        //DataSupport.deleteAll(Type.class);
        for (int i = 0; i < informationPoints.length; i++)
        {
            informationPointList.add(informationPoints[i]);
        }
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
