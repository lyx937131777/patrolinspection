package com.example.patrolinspection;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.PatrolLineAdapter;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PatrolLineActivity extends AppCompatActivity
{
    private List<PatrolSchedule> patrolScheduleList = new ArrayList<>();
    private PatrolLineAdapter adapter;

    private String planID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_line);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initPL();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PatrolLineAdapter(patrolScheduleList);
        recyclerView.setAdapter(adapter);
    }

    private void initPL()
    {
        Date now = new Date(System.currentTimeMillis());
        List<PatrolPlan> patrolPlanList = LitePal.where("patrolPlanType = ?","specialDate").find(PatrolPlan.class);
        boolean flag = true;
        for(PatrolPlan patrolPlan: patrolPlanList){
            Date startDate = Utility.stringToDate(patrolPlan.getStartDate());
            Date endDate = Utility.stringToDate(patrolPlan.getEndDate());
            if(now.after(startDate) && now.before(endDate)){
                flag = false;
                planID = patrolPlan.getInternetID();
                setTitle(patrolPlan.getName());
                break;
            }
        }
        if(flag){
            int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            LogUtil.e("PatrolLineActivity","week: "+week);
            PatrolPlan patrolPlan = LitePal.where("patrolPlanType = ?", MapUtil.getPlanType(String.valueOf(week))).findFirst(PatrolPlan.class);
            planID = patrolPlan.getInternetID();
            this.setTitle(patrolPlan.getName());
            LogUtil.e("PatrolLineActivity","planID: "+planID +"          name: "+patrolPlan.getName());
        }


        patrolScheduleList.clear();
        List<PatrolSchedule> tempList = LitePal.where("patrolPlanId = ?",planID).find(PatrolSchedule.class);
        List<String> lineList = new ArrayList<>();
        for(PatrolSchedule patrolSchedule : tempList){
            if(!lineList.contains(patrolSchedule.getPatrolLineId())){
                patrolScheduleList.add(patrolSchedule);
                lineList.add(patrolSchedule.getPatrolLineId());
            }
        }
// 用SQL语句时 字段不能带id ID Id
//        Cursor cursor = LitePal.findBySQL("select distinct patrolLine from PatrolSchedule where patrolPlan = ?",planID);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                LogUtil.e("PatrolLineActivity","111111111111111111");
//                String lineID = cursor.getString(cursor.getColumnIndex("patrolLine"));
//                PatrolSchedule patrolSchedule = new PatrolSchedule();
//                patrolSchedule.setPatrolLineId(lineID);
//                patrolSchedule.setPatrolPlanId(planID);
//                patrolScheduleList.add(patrolSchedule);
//                LogUtil.e("PatrolLineActivity",patrolSchedule.getPatrolLineId());
//            } while (cursor.moveToNext());
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode){
            case 0:
                if(resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
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
