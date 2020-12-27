package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.LineListAdapter;
import com.example.patrolinspection.adapter.PointListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//数据更新的信息点列表界面
public class PointListActivity extends AppCompatActivity
{
    private String lineID;
    private PatrolLine patrolLine;

    private List<PatrolIP> patrolIPList = new ArrayList<>();
    private PointListAdapter adapter;

    private Button update;
    private DataUpdatingPresenter dataUpdatingPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);

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
        lineID = intent.getStringExtra("line");
        patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
        actionBar.setTitle(patrolLine.getPatrolLineName());

        initPatrolIP();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PointListAdapter(patrolIPList);
        recyclerView.setAdapter(adapter);

        update = findViewById(R.id.point_updating);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updatePatrolLine();
            }
        });
    }

    private void initPatrolIP()
    {
        patrolIPList.clear();
        patrolIPList.addAll(LitePal.where("patrolLineID = ?",lineID).find(PatrolIP.class));
        Collections.sort(patrolIPList, new Comparator<PatrolIP>()
        {
            @Override
            public int compare(PatrolIP o1, PatrolIP o2)
            {
                return Integer.parseInt(o1.getOrderNo())-Integer.parseInt(o2.getOrderNo());
            }
        });
    }

    public void refresh()
    {
        initPatrolIP();
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
