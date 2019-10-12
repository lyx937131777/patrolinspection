package com.example.patrolinspection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.patrolinspection.adapter.TypeAdapter;
import com.example.patrolinspection.db.Type;
import com.example.patrolinspection.util.LogUtil;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private Type[] types = {new Type("巡检", R.drawable.patrol_inspection,R.drawable.patrol_inspection_press,"patrolInspection"), new Type("签到/签退", R.drawable.sign,R.drawable.sign_press,"sign")
            , new Type("公告", R.drawable.notice,R.drawable.notice_press,"notice"), new Type("发现异常", R.drawable.event_found,R.drawable.event_found_press,"eventFound"),
            new Type("异常事件列表", R.drawable.event_list,R.drawable.event_list_press,"eventList"), new Type("数据更新", R.drawable.data_updating,R.drawable.data_updating_press,"dataUpdating"),
            new Type("保安注册", R.drawable.security_staff,R.drawable.security_staff_press,"securityStaff"), new Type("信息点注册", R.drawable.information_point,R.drawable.information_point_press,"informationPoint"),
            new Type("系统参数", R.drawable.system_parameter,R.drawable.system_parameter_press,"systemParameter")};
    private List<Type> typeList = new ArrayList<>();
    private TypeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        LogUtil.e("Notice main",timestamp.toString());
        initTypes();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TypeAdapter(typeList);
        recyclerView.setAdapter(adapter);
    }

    private void initTypes()
    {
        typeList.clear();
        //DataSupport.deleteAll(Type.class);
        for (int i = 0; i < types.length; i++)
        {
            typeList.add(types[i]);
//            Type newtype = DataSupport.where("TypeName = ?",types[i].getTypeName()).findFirst(Type.class);
//            if(newtype == null)
//            {
//                types[i].save();
//                LogUtil.e("MainActivity","=========save==========");
//            }else
//            {
//                LogUtil.e("MainActivity","Not save!!!!!!!!!!!");
//            }
        }
    }
}
