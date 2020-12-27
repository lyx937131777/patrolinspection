package com.example.patrolinspection;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.SchoolEventPresenter;
import com.example.patrolinspection.fragment.adapter.SchoolPagerAdapter;

import org.litepal.LitePal;

public class SchoolEventActivity extends AppCompatActivity
{
    private String policeID;
    private TextView nameText;
    private Button logout;

    private SchoolEventPresenter schoolEventPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_event);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        schoolEventPresenter = myComponent.schoolEventPresenter();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        policeID = pref.getString("schoolPolice","null");
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("newSchoolEvent",false);
        editor.apply();

        nameText = findViewById(R.id.name);
        logout = findViewById(R.id.logout);

        Police police = LitePal.where("internetID = ?",policeID).findFirst(Police.class);
        nameText.setText(police.getRealName());
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                schoolEventPresenter.logout();
            }
        });


        SchoolPagerAdapter schoolPagerAdapter = new SchoolPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(schoolPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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
