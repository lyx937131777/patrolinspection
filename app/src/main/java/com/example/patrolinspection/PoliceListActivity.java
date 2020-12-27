package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.PlanListAdapter;
import com.example.patrolinspection.adapter.PoliceListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

//数据更新的巡检人员界面
public class PoliceListActivity extends AppCompatActivity
{
    private List<Police> policeList = new ArrayList<>();
    private PoliceListAdapter adapter;

    private Button update;
    private DataUpdatingPresenter dataUpdatingPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        initPolice();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PoliceListAdapter(policeList);
        recyclerView.setAdapter(adapter);

        update = findViewById(R.id.police_updating);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updatePolice();
            }
        });
    }

    private void initPolice()
    {
        policeList.clear();
        policeList.addAll(LitePal.findAll(Police.class));
    }

    public void refresh()
    {
        initPolice();
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
