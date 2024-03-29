package com.example.patrolinspection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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

import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.PoliceRegisterPresenter;
import com.example.patrolinspection.util.FileUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.mroland.android.apps.imagedecoder.BitmapImageFactory;

//保安注册界面
public class PoliceRegisterActivity extends AppCompatActivity
{
    private TextView nameText;
    private TextView sexText;
    private TextView nationText;
    private TextView securityText;
    private EditText telEdit;
    private Spinner dutySpinner;
    private Button register;
    private ImageView photo;

    private String name;
    private String sex;
    private String nation;
    private String securityCard;
    private String identityCard;
    private String birth;
    private byte[] photoInfoBytes;
    private Bitmap resizeBmp;
    private String icCard;

    private List<String> dutyList = new ArrayList<>();
    private String duty;

    private ArrayAdapter<String> arrayAdapter;

    private PoliceRegisterPresenter policeRegisterPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_register);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        policeRegisterPresenter = myComponent.policeRegisterPresenter();

        nameText = findViewById(R.id.police_name);
        sexText = findViewById(R.id.police_sex);
        nationText = findViewById(R.id.police_nation);
        securityText = findViewById(R.id.police_security);
        telEdit = findViewById(R.id.police_tel);
        dutySpinner = findViewById(R.id.police_duty);
        register = findViewById(R.id.register);
        photo = findViewById(R.id.photo);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        sex = intent.getStringExtra("sex");
        nation = intent.getStringExtra("nation");
        securityCard = intent.getStringExtra("securityCard");
        identityCard = intent.getStringExtra("identityCard");
        birth = intent.getStringExtra("birth");
        icCard = intent.getStringExtra("icCard");

        nameText.setText(name);
        sexText.setText(sex);
        nationText.setText(nation);
        securityText.setText(securityCard);

        photoInfoBytes = intent.getByteArrayExtra("photo");
        if(photoInfoBytes != null)
        {
            //BitmapImageFactory类需要右键项目Properties--Android--Library--Add... 添加ImageDecoderService项目
            BitmapImageFactory bif = BitmapImageFactory.get(photoInfoBytes);
            Bitmap bm = null;
            if (bif != null)
            {
                bm = bif.getImage();
                if (bm != null)
                {
                    Matrix matrix = new Matrix();
                    matrix.postScale(2.0f, 2.0f); //长和宽放大缩小的比例
                    resizeBmp = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                    photo.setImageBitmap(resizeBmp);
                }
            }
        }

        initDutyList();
        arrayAdapter = new ArrayAdapter<>(PoliceRegisterActivity.this, android.R.layout.simple_spinner_item, dutyList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dutySpinner.setAdapter(arrayAdapter);

        telEdit.setInputType(InputType.TYPE_CLASS_PHONE);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (ContextCompat.checkSelfPermission(PoliceRegisterActivity.this, Manifest
                        .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(PoliceRegisterActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else
                {
                    savePhoto();
                }
            }
        });
    }

    private void savePhoto(){
        long time = System.currentTimeMillis();
        File temp =  null;
        try{
            temp = FileUtil.saveFile(resizeBmp,time+".jpeg");
            LogUtil.e("PoliceRegister","abosolutePath: " + temp.getAbsolutePath());
            LogUtil.e("PoliceRegister","canonicalPath: " + temp.getCanonicalPath());
            LogUtil.e("PoliceRegister","Path: " + temp.getPath());
            LogUtil.e("PoliceRegister","parentPath: " + temp.getParent());
            LogUtil.e("PoliceRegister","Name: " + temp.getName());
        }catch (IOException e){
            e.printStackTrace();
            LogUtil.e("PoliceRegister","文件创建失败");
        }
        policeRegisterPresenter.register(name,securityCard,icCard,identityCard,birth,sex,nation,telEdit.getText().toString(),duty,temp);
    }

    private void initDutyList()
    {
        dutyList.clear();
        for(int i = 1; i <= 7; i++){
            dutyList.add(MapUtil.getDuty(String.valueOf(i)));
        }
        dutySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                duty = MapUtil.getDuty(dutyList.get(i));
                LogUtil.e("PoliceRegister",duty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePhoto();
                } else {
                    Toast.makeText(this, "你拒绝了权限请求！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
