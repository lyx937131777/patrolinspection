package com.example.patrolinspection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import com.example.patrolinspection.presenter.SignInOutPresenter;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

public class SignInOutActivity extends AppCompatActivity
{
    //photo
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    private String imagePath = null;

    private String type;
    private String title;
    private String attendanceType;
    private String policeID;
    private boolean isFace;

    private TextView nameText;
    private ImageView photoButton;

    private SignInOutPresenter signInOutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_out);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        signInOutPresenter = myComponent.signInOutPresenter();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        type = intent.getStringExtra("type");
        attendanceType = intent.getStringExtra("attendanceType");
        policeID = intent.getStringExtra("police");
        Police police = LitePal.where("internetID = ?",policeID).findFirst(Police.class);
        nameText = findViewById(R.id.sign_name);
        nameText.setText(police.getRealName());

        LogUtil.e("SignInOutActivity",title + " " + type + " " + attendanceType + " " + policeID);

        final Button signInOut = findViewById(R.id.sign_in_out);
        signInOut.setText(title);
        signInOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!isFace){
                    signInOutPresenter.signInOut(policeID,type,attendanceType);
                } else if(imagePath != null){
                    signInOutPresenter.signInOut(policeID,imagePath,type,attendanceType);
                }else {
                    Toast.makeText(SignInOutActivity.this,"请先拍照！",Toast.LENGTH_LONG).show();
                }

            }
        });

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
                    imageUri = FileProvider.getUriForFile(SignInOutActivity.this,
                            "com.example.patrolinspection.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(intent, TAKE_PHOTO);
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
                        Glide.with(SignInOutActivity.this).load(imageUri).into(photoButton);
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
