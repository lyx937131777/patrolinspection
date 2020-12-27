package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.LineListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

//数据更新的巡检线路列表界面
public class LineListActivity extends AppCompatActivity
{

    private List<PatrolLine> patrolLineList = new ArrayList<>();
    private LineListAdapter adapter;

    private Button update;
    private DataUpdatingPresenter dataUpdatingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        initPatrolLine();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LineListAdapter(patrolLineList);
        recyclerView.setAdapter(adapter);

        update = findViewById(R.id.line_updating);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updatePatrolLine();
            }
        });

    }

    private void initPatrolLine()
    {
        patrolLineList.clear();
        patrolLineList.addAll(LitePal.findAll(PatrolLine.class));
    }

    public void refresh()
    {
        initPatrolLine();
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
