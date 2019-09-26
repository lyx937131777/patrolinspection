package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.NoticeAdapter;
import com.example.patrolinspection.db.Notice;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity
{
    private Notice[] notices = {new Notice("2019-9-1", "标题1","11111111111111111111111111111111111111111111111" +
            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"),
            new Notice("2019-1-1","标题2","22222222222222222222222222222222222222222222222222222222222222222" +
            "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"),
            new Notice("2018-10-1","标题3","333333333333333333333333333333333333333333333333")};
    private List<Notice> noticeList = new ArrayList<>();
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initNotice();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(adapter);
    }

    private void initNotice()
    {
        noticeList.clear();
        //DataSupport.deleteAll(Type.class);
        for (int i = 0; i < notices.length; i++)
        {
            noticeList.add(notices[i]);
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
