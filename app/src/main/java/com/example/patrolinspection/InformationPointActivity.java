package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.InformationPointPresenter;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.NfcUtil;

public class InformationPointActivity extends AppCompatActivity
{

    private String id;
    private EditText nameText;
    private EditText longitudeText;
    private EditText latitudeText;
    private EditText heightText;
    private EditText floorText;
    private Button register;

    private InformationPointPresenter informationPointPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_point);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        informationPointPresenter = myComponent.informationPointPresenter();

        Intent intent = getIntent();
        //id = intent.getStringExtra("id");
        id = NfcUtil.getID(getIntent());
        TextView idText = findViewById(R.id.ip_id);
        idText.setText(id);

        nameText = findViewById(R.id.ip_name);
        longitudeText = findViewById(R.id.ip_longitude);
        latitudeText = findViewById(R.id.ip_latitude);
        heightText = findViewById(R.id.ip_height);
        floorText = findViewById(R.id.ip_floor);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               informationPointPresenter.register(id, nameText.getText().toString(), longitudeText.getText().toString(),
                       latitudeText.getText().toString(), heightText.getText().toString(), floorText.getText().toString());
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
