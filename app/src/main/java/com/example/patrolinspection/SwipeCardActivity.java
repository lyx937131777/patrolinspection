package com.example.patrolinspection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;

public class SwipeCardActivity extends AppCompatActivity
{
    private String type;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_card);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
//        this.setTitle(intent.getStringExtra("title")); 也可以
        title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        type = intent.getStringExtra("type");

        CardView swipeCard = findViewById(R.id.swipe_card);
        swipeCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch (type){
                    case "signIn":
                    case "signOut":{
                        Intent intent = new Intent(SwipeCardActivity.this, SignInOutActivity.class);
                        intent.putExtra("type",type);
                        intent.putExtra("title",title);
                        startActivity(intent);
                        break;
                    }
                    case "eventFound":{
                        Intent intent = new Intent(SwipeCardActivity.this,EventFoundActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "securityStaff":{

                        break;
                    }
                    case "informationPoint":{
                        Intent intent = new Intent(SwipeCardActivity.this,InformationPointActivity.class);
                        startActivity(intent);
                        break;
                    }

                }
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
