package com.example.patrolinspection;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.patrolinspection.adapter.NoticeAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Notice;
import com.example.patrolinspection.presenter.NoticePresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

//公告界面
public class NoticeActivity extends AppCompatActivity
{
    private List<Notice> noticeList = new ArrayList<>();
    private NoticeAdapter adapter;

    private NoticePresenter noticePresenter;

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

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        noticePresenter = myComponent.noticePresenter();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("newNotice",false);
        editor.apply();

        initNotice();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(adapter);

        noticePresenter.updateNotice(noticeList,adapter);
    }

    private void initNotice()
    {
        noticeList.clear();
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
