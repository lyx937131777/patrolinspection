package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.LogUtil;

public class DataUpdatingActivity extends AppCompatActivity
{
    private Button updating;
    private CardView planCard;
    private CardView lineCard;
    private CardView eventCard;
    private CardView policeCard;
    private CardView uploadCard;

    private DataUpdatingPresenter dataUpdatingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_updating);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        planCard = findViewById(R.id.du_plan);
        planCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DataUpdatingActivity.this, PlanListActivity.class);
                startActivity(intent);
            }
        });

        lineCard = findViewById(R.id.du_line);
        lineCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DataUpdatingActivity.this, LineListActivity.class);
                startActivity(intent);
            }
        });

        eventCard = findViewById(R.id.du_event);
        eventCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DataUpdatingActivity.this, EventListActivity.class);
                intent.putExtra("type","eventType");
                startActivity(intent);
            }
        });

        policeCard = findViewById(R.id.du_police);
        policeCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DataUpdatingActivity.this, PoliceListActivity.class);
                startActivity(intent);
            }
        });
        uploadCard = findViewById(R.id.du_upload);
        uploadCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DataUpdatingActivity.this, UploadListActivity.class);
                startActivity(intent);
            }
        });

        updating = findViewById(R.id.data_updating);
        updating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updateAll();
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
