package com.example.patrolinspection;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.patrolinspection.adapter.EventAdapter;
import com.example.patrolinspection.adapter.PatrolLineAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.MapUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity
{
    private List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private String type;

    private Button update;

    private DataUpdatingPresenter dataUpdatingPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

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
        type = intent.getStringExtra("type");
        if(type.equals("eventName")){
            String title = intent.getStringExtra("eventType");
            actionBar.setTitle(MapUtil.getEventType(title));
        }

        initEvent();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new EventAdapter(eventList,type);
        recyclerView.setAdapter(adapter);

        update = findViewById(R.id.event_updating);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dataUpdatingPresenter.updateEvent();
            }
        });
    }

    private void initEvent()
    {
        eventList.clear();
        if(type.equals("eventType")){
            Cursor cursor = LitePal.findBySQL("select distinct type from Event");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    Event event = new Event();
                    event.setType(type);
                    eventList.add(event);
                } while (cursor.moveToNext());
            }
        }else{
            Intent intent = getIntent();
            String title = intent.getStringExtra("eventType");
            eventList.addAll(LitePal.where("type = ?",title).find(Event.class));
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
