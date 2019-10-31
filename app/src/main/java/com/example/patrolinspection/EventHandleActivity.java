package com.example.patrolinspection;

import android.content.Intent;
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
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.EventHandlePresenter;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventHandleActivity extends AppCompatActivity
{
    //photo
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String imagePath = null;

    //参数
    private String policeID;
    private String eventRecordID;

    //控件
    private ImageView photoButton;
    private EditText detailText;
    private Button eventHandle;
    private TextView nameText;
    private TextView dutyText;
    private Spinner typeSpinner;
    private Spinner reportSpinner;

    private List<String> typeList = new ArrayList<>();
    private List<String> reportList = new ArrayList<>();

    private String type;
    private String report;

    private ArrayAdapter<String> arrayTypeAdapter;
    private ArrayAdapter<String> arrayReportAdapter;


    private EventHandlePresenter eventHandlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_handle);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        eventHandlePresenter = myComponent.eventHandlePresenter();

        Intent intent = getIntent();
        policeID = intent.getStringExtra("police");
        Police police = LitePal.where("internetID = ?",policeID).findFirst(Police.class);
        nameText = findViewById(R.id.eh_name);
        nameText.setText(police.getRealName());
        dutyText = findViewById(R.id.eh_duty);
        dutyText.setText(MapUtil.getDuty(police.getMainDutyId()));

        typeSpinner = findViewById(R.id.eh_type);
        reportSpinner = findViewById(R.id.eh_report);

        initList();
        arrayTypeAdapter = new ArrayAdapter<String>(EventHandleActivity.this,android.R.layout.simple_spinner_item,typeList);
        arrayReportAdapter = new ArrayAdapter<String>(EventHandleActivity.this,android.R.layout.simple_spinner_item,reportList);
        arrayTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayReportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayTypeAdapter);
        reportSpinner.setAdapter(arrayReportAdapter);

        eventRecordID = intent.getStringExtra("eventRecord");
        detailText = findViewById(R.id.eh_detail);
        photoButton = findViewById(R.id.photo);
//        if(photoType.equals("forbid")){
//            photoButton.setVisibility(View.GONE);
//        }
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 创建File对象，用于存储拍照后的图片
                long time = System.currentTimeMillis();
                File outputImage = new File(getExternalCacheDir(), time+".jpeg");
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
                    imageUri = FileProvider.getUriForFile(EventHandleActivity.this,
                            "com.example.patrolinspection.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        eventHandle = findViewById(R.id.event_handle);
        eventHandle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(imagePath != null){
                    eventHandlePresenter.postHandleRecord(policeID,imagePath,eventRecordID,type,report,detailText.getText().toString());
                }else{
                    Toast.makeText(EventHandleActivity.this, "请先拍照！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initList()
    {
        typeList.clear();
        typeList.add(MapUtil.getHandleType("disposal"));
        typeList.add(MapUtil.getHandleType("report"));
        typeList.add(MapUtil.getHandleType("end"));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                type = MapUtil.getHandleType(typeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        //TODO question2
        reportList.clear();
        reportList.add("不选");
        reportList.add("保安队长");
        reportList.add("物业管理");
        reportList.add("居委会");
        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                report = reportList.get(position);
                if(report.equals("不选")){
                    report = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
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
                        Glide.with(EventHandleActivity.this).load(imageUri).into(photoButton);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else{
                    imagePath = null;
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
