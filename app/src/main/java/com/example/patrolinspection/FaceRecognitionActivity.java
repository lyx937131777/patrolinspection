package com.example.patrolinspection;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.presenter.FaceRecognitionPresenter;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

public class FaceRecognitionActivity extends AppCompatActivity
{
    //photo
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String imagePath = null;

    private String policeID;
    private boolean isFace;

    private ImageView photoButton;
    private TextView nameText;
    private TextView dutyText;

    private FaceRecognitionPresenter faceRecognitionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        faceRecognitionPresenter = myComponent.faceRecognitionPresenter();
        policeID = getIntent().getStringExtra("police");
        Police police = LitePal.where("internetID = ?",policeID).findFirst(Police.class);
        nameText = findViewById(R.id.fr_name);
        nameText.setText(police.getRealName());
        dutyText = findViewById(R.id.fr_duty);
        dutyText.setText(MapUtil.getDuty(police.getMainDutyId()));


        photoButton = findViewById(R.id.photo);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        isFace = pref.getBoolean("isFace",false);
        if(!isFace){
            photoButton.setVisibility(View.GONE);
        }else{
            Glide.with(this).load(R.drawable.scan).into(photoButton);
        }

        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ContextCompat.checkSelfPermission(FaceRecognitionActivity.this, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(FaceRecognitionActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else
                {
                    takePhoto();
                }
            }
        });

        Button startPatrol = findViewById(R.id.start_patrol);
        startPatrol.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isFace){
                    faceRecognitionPresenter.startPatrol(policeID, getIntent().getStringExtra("schedule"));
                }else if(imagePath != null){
                    imagePath = Utility.compressImagePathToImagePath(imagePath);
                    faceRecognitionPresenter.startPatrol(policeID, imagePath,getIntent().getStringExtra("schedule"));
                }else{
                    Toast.makeText(FaceRecognitionActivity.this,"请先拍照！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case 0:
                if(resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
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
                        Glide.with(FaceRecognitionActivity.this).load(imageUri).into(photoButton);
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
    private void takePhoto(){
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
            imageUri = FileProvider.getUriForFile(FaceRecognitionActivity.this,
                    "com.example.patrolinspection.fileprovider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "你拒绝了权限请求！", Toast.LENGTH_LONG).show();
                }
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
