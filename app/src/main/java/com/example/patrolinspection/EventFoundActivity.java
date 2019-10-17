package com.example.patrolinspection;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.EventFoundPresenter;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventFoundActivity extends AppCompatActivity
{
    //photo
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String imagePath = null;

    private String type;
    private String lineID;
    private String recordID;
    private String pointID;
    private String policeID;

    private Spinner efClass;
    private Spinner efType;
    private ImageView photoButton;
    private EditText detailText;
    private Button eventFound;
    private TextView nameText;
    private TextView dutyText;

    private List<String> efClassList = new ArrayList<>();
    private List<String> efTypeList = new ArrayList<>();

    private String classString;
    private String typeString;

    private ArrayAdapter<String> arrayClassAdapter;
    private ArrayAdapter<String> arrayTypeAdapter;

    private EventFoundPresenter eventFoundPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_found);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        eventFoundPresenter = myComponent.eventFoundPresenter();

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if(type.equals("normal")){
            recordID = "";
            pointID = "";
        }else{
            recordID = intent.getStringExtra("record");
            pointID = intent.getStringExtra("point");
        }
        policeID = intent.getStringExtra("police");
        Police police = LitePal.where("internetID = ?",policeID).findFirst(Police.class);
        nameText = findViewById(R.id.ef_name);
        nameText.setText(police.getRealName());
        dutyText = findViewById(R.id.ef_duty);
        dutyText.setText(MapUtil.getDuty(police.getMainDutyId()));


        efClass = findViewById(R.id.ef_class);
        efType = findViewById(R.id.ef_type);

        arrayTypeAdapter = new ArrayAdapter<String>(EventFoundActivity.this,android.R.layout.simple_spinner_item,efTypeList);
        initEventList();
        arrayClassAdapter = new ArrayAdapter<String>(EventFoundActivity.this,android.R.layout.simple_spinner_item,efClassList);
        arrayClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        efClass.setAdapter(arrayClassAdapter);
        efType.setAdapter(arrayTypeAdapter);

        detailText = findViewById(R.id.ef_detail);
        photoButton = findViewById(R.id.photo);
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 创建File对象，用于存储拍照后的图片
                long time = System.currentTimeMillis();
                File outputImage = new File(getExternalCacheDir(), time+".png");
                imagePath = outputImage.getAbsolutePath();
                try
                {
                    if (outputImage.exists())
                    {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT < 24)
                {
                    imageUri = Uri.fromFile(outputImage);
                } else
                {
                    imageUri = FileProvider.getUriForFile(EventFoundActivity.this,
                            "com.example.patrolinspection.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        eventFound = findViewById(R.id.event_found);
        eventFound.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                eventFoundPresenter.postEventRecord(policeID,imagePath,typeString,recordID,pointID,detailText.getText().toString());
            }
        });
    }

    private void initEventList()
    {
        efClassList.clear();
        if(type.equals("normal")){

            Cursor cursor = LitePal.findBySQL("select distinct type from Event");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    efClassList.add(MapUtil.getEventType(type));
                } while (cursor.moveToNext());
            }
        }else{
            //TODO
            PatrolRecord patrolRecord = LitePal.where("internetID = ?", recordID).findFirst(PatrolRecord.class);
            PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolRecord.getPatrolScheduleId()).findFirst(PatrolSchedule.class);
            PatrolLine patrolLine = LitePal.where("internetID = ?",patrolSchedule.getPatrolLineId()).findFirst(PatrolLine.class);
            for(String evenID: patrolLine.getEventInfoIds()){
                Event event = LitePal.where("internetID = ?",evenID).findFirst(Event.class);
                if(!efClassList.contains(MapUtil.getEventType(event.getType()))){
                    efClassList.add(MapUtil.getEventType(event.getType()));
                }
            }
        }

//        classString = MapUtil.getEventType(efClassList.get(0));
//        changeClass();
        efClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                classString = MapUtil.getEventType(efClassList.get(i));
                LogUtil.e("EventFoundActivity",classString);
                changeClass();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        efType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                typeString = efTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    private void changeClass(){
        List<Event> eventList = LitePal.where("type = ?",classString).find(Event.class);
        efTypeList.clear();
        if(type.equals("normal")){
            for(Event event : eventList){
                efTypeList.add(event.getName());
            }

        }else{
            //TODO
            PatrolRecord patrolRecord = LitePal.where("internetID = ?", recordID).findFirst(PatrolRecord.class);
            PatrolSchedule patrolSchedule = LitePal.where("internetID = ?",patrolRecord.getPatrolScheduleId()).findFirst(PatrolSchedule.class);
            PatrolLine patrolLine = LitePal.where("internetID = ?",patrolSchedule.getPatrolLineId()).findFirst(PatrolLine.class);
            for(Event event : eventList){
                if(patrolLine.getEventInfoIds().contains(event.getInternetID())){
                    efTypeList.add(event.getName());
                }
            }
        }
        arrayTypeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        LogUtil.e("camera", getContentResolver().openInputStream(imageUri).toString());
                        LogUtil.e("camera", "imageUri:" + imageUri.toString());
                        LogUtil.e("camera","imagePath:"+ imagePath);
//                        photoButton.setImageBitmap(bitmap);
                        Glide.with(EventFoundActivity.this).load(imageUri).into(photoButton);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
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
