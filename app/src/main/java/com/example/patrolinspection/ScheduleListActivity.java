package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.ScheduleListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListActivity extends AppCompatActivity
{
    private String planID;
    private PatrolPlan patrolPlan;

    private List<PatrolSchedule> patrolScheduleList = new ArrayList<>();
    private ScheduleListAdapter adapter;

    private Button update;
    private DataUpdatingPresenter dataUpdatingPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        Intent intent = getIntent();
        planID = intent.getStringExtra("plan");
        LogUtil.e("DataUpdatingSchedule","planID: "+planID);
        patrolPlan = LitePal.where("internetID = ?", planID).findFirst(PatrolPlan.class);
        if(patrolPlan != null){
            LogUtil.e("DataUpdating",patrolPlan.getInternetID() + " " + patrolPlan.getName());
        }
        actionBar.setTitle(patrolPlan.getName());

        initPatrolSchedule();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ScheduleListAdapter(patrolScheduleList);
        recyclerView.setAdapter(adapter);

        update = findViewById(R.id.schedule_updating);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updatePatrolPlan();
            }
        });
    }

    private void initPatrolSchedule()
    {
        patrolScheduleList.clear();
        patrolScheduleList.addAll(LitePal.where("patrolPlanId = ?",planID).find(PatrolSchedule.class));
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
