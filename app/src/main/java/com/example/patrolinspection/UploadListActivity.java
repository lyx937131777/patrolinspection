package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.patrolinspection.adapter.PlanListAdapter;
import com.example.patrolinspection.adapter.PointListAdapter;
import com.example.patrolinspection.adapter.UploadListAdapter;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.presenter.DataUpdatingPresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//上传列表界面
public class UploadListActivity extends AppCompatActivity
{
    private List<PatrolRecord> patrolrecordlist = new ArrayList<>();
    private UploadListAdapter adapter;

    private Button clearButton;
    private Button uploadButton;

    private DataUpdatingPresenter dataUpdatingPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        dataUpdatingPresenter = myComponent.dataUpdatingPresenter();

        initPatrolRecord();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new UploadListAdapter(patrolrecordlist);
        recyclerView.setAdapter(adapter);

        uploadButton = findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                List<PatrolRecord> patrolRecordList = LitePal.where("upload = ?","0").find(PatrolRecord.class);
                if(patrolRecordList.size() == 0){
                    Toast.makeText(UploadListActivity.this, "所有记录均已上传",Toast.LENGTH_LONG).show();
                }else{
                    dataUpdatingPresenter.uploadPatrolRecordPhoto();
                }
            }
        });

        clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LogUtil.e("UploadListActivity","clearButton click!");
                LitePal.deleteAll(PatrolRecord.class,"upload = ?","1");
                refresh();
                Toast.makeText(UploadListActivity.this, "已清除所有已上传的记录",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initPatrolRecord()
    {
        patrolrecordlist.clear();
        patrolrecordlist.addAll(LitePal.findAll(PatrolRecord.class));
        Collections.sort(patrolrecordlist, new Comparator<PatrolRecord>()
        {
            @Override
            public int compare(PatrolRecord o1, PatrolRecord o2)
            {
                //若为正o1在后
                if((o1.isUpload() && o2.isUpload()) || (!o1.isUpload() && !o2.isUpload())){
                    return Integer.parseInt(o2.getInternetID())-Integer.parseInt(o1.getInternetID());
                }else if(o1.isUpload()){
                    return 1;
                }else{
                    return -1;
                }

            }
        });
    }

    public void refresh()
    {
        initPatrolRecord();
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
