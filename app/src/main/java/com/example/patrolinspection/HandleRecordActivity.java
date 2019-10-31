package com.example.patrolinspection;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.HandleRecordAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.HandleRecord;
import com.example.patrolinspection.presenter.HandleRecordPresenter;

import java.util.ArrayList;
import java.util.List;

public class HandleRecordActivity extends AppCompatActivity
{
    private String eventRecordID;
    private String type;

    private List<HandleRecord> handleRecordList = new ArrayList<>();
    private HandleRecordAdapter adapter;

    private Button handleButton;

    private HandleRecordPresenter handleRecordPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_record);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        handleRecordPresenter = myComponent.handleRecordPresenter();

        Intent intent = getIntent();
        eventRecordID = intent.getStringExtra("eventRecord");
        type = intent.getStringExtra("type");

        initHandleRecord();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new HandleRecordAdapter(handleRecordList);
        recyclerView.setAdapter(adapter);

        handleButton = findViewById(R.id.handle);
        if(type.equals("ended")){
            handleButton.setVisibility(View.GONE);
        }
        handleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent startIntent = new Intent(HandleRecordActivity.this, SwipeNfcActivity.class);
                startIntent.putExtra("type","eventHandle");
                startIntent.putExtra("eventRecord",eventRecordID);
                startActivityForResult(startIntent,0);
            }
        });

        handleRecordPresenter.updateRecord(handleRecordList,adapter,eventRecordID);

    }

    private void initHandleRecord()
    {
        handleRecordList.clear();
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
