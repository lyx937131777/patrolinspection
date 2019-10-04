package com.example.patrolinspection;

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

public class NoticeActivity extends AppCompatActivity
{
//    private Notice[] notices = {new Notice("2019-9-1", "标题1","11111111111111111111111111111111111111111111111" +
//            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
//            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
//            "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"),
//            new Notice("2019-1-1","标题2","22222222222222222222222222222222222222222222222222222222222222222" +
//            "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "22222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "2222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222" +
//                    "222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222"),
//            new Notice("2018-10-1","标题3","333333333333333333333333333333333333333333333333")};
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
        noticeList.addAll(LitePal.findAll(Notice.class));
        LogUtil.e("NoticeActivity", "noticeList Size: " + noticeList.size());
        for(Notice notice : noticeList){
            LogUtil.e("NoticeActivity",notice.getInternetID() + " " + notice.getTitle() + " " + notice.getContent());
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
