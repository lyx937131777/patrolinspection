package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.TypeAdapter;
import com.example.patrolinspection.db.Type;

import java.util.ArrayList;
import java.util.List;

public class InformationRegisterActivity extends AppCompatActivity
{
    private Type[] types = {new Type("保安注册", R.drawable.security_staff,R.drawable.security_staff_press, "securityStaff"),
            new Type("信息点注册", R.drawable.information_point,R.drawable.information_point_press,"informationPoint")
    };
    private List<Type> typeList = new ArrayList<>();
    private TypeAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_register);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initTypes();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TypeAdapter(typeList);
        recyclerView.setAdapter(adapter);

    }

    private void initTypes()
    {
        typeList.clear();
        for (int i = 0; i < types.length; i++)
        {
            typeList.add(types[i]);
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
