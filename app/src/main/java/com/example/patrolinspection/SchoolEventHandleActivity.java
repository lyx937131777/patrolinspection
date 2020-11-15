package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.SchoolEventRecord;
import com.example.patrolinspection.presenter.SchoolEventHandlePresenter;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

public class SchoolEventHandleActivity extends AppCompatActivity
{
    private SchoolEventRecord schoolEventRecord;
    private String schoolEventRecordID;

    private TextView timeText;
    private TextView typeText;
    private TextView stateText;
    private ImageView photo;

    private Button receiveButton;
    private Button concernButton;
    private Button unfoundButton;
    private Button dangerButton;
    private Button undangerButton;


    private SchoolEventHandlePresenter schoolEventHandlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_event_handle);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        schoolEventHandlePresenter = myComponent.schoolEventHandlePresenter();

        schoolEventRecord = (SchoolEventRecord) getIntent().getSerializableExtra("schoolEventRecord");
        schoolEventRecordID = schoolEventRecord.getInternetID();

        timeText = findViewById(R.id.se_time);
        typeText = findViewById(R.id.se_type);
        stateText = findViewById(R.id.se_state);

        timeText.setText(TimeUtil.timeStampToString(schoolEventRecord.getOccurrenceTime(),"yyyy-MM-dd HH:mm"));
        typeText.setText(MapUtil.getSchoolEventType(schoolEventRecord.getSchoolEventType()));
        stateText.setText(schoolEventRecord.getState());

        photo = findViewById(R.id.photo);
        Glide.with(this).load(HttpUtil.getResourceURL(schoolEventRecord.getPhoto())).into(photo);

        receiveButton = findViewById(R.id.receive);
        concernButton = findViewById(R.id.concern);
        unfoundButton = findViewById(R.id.unfound);
        undangerButton = findViewById(R.id.undanger);
        dangerButton = findViewById(R.id.danger);

        receiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventHandlePresenter.handleSchoolEvent(schoolEventRecordID,"receive");
            }
        });

        concernButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventHandlePresenter.handleSchoolEvent(schoolEventRecordID,"concern");
            }
        });

        unfoundButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventHandlePresenter.handleSchoolEvent(schoolEventRecordID,"unfound");
            }
        });

        dangerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventHandlePresenter.handleSchoolEvent(schoolEventRecordID,"danger");
            }
        });

        undangerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventHandlePresenter.handleSchoolEvent(schoolEventRecordID,"undanger");
            }
        });

        if(schoolEventRecord.needToReceive()){
            receiveButton.setVisibility(View.VISIBLE);
        }else if(schoolEventRecord.needToConcern()){
            concernButton.setVisibility(View.VISIBLE);
            unfoundButton.setVisibility(View.VISIBLE);
        }else if(schoolEventRecord.needToHandle()){
            undangerButton.setVisibility(View.VISIBLE);
            dangerButton.setVisibility(View.VISIBLE);
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
