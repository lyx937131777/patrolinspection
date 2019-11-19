package com.example.patrolinspection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.example.patrolinspection.dagger2.DaggerMyComponent;
import com.example.patrolinspection.dagger2.MyComponent;
import com.example.patrolinspection.dagger2.MyModule;
import com.example.patrolinspection.presenter.InformationPointPresenter;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.NfcUtil;

import java.util.ArrayList;
import java.util.List;

public class InformationPointActivity extends AppCompatActivity
{
    private LocationClient mLocationClient;

    private String id;
    private EditText nameText;
    private EditText longitudeText;
    private EditText latitudeText;
    private EditText heightText;
    private EditText floorText;
    private Button register;

    private InformationPointPresenter informationPointPresenter;
    private class MyLocationListener extends BDAbstractLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location) {
            longitudeText.setText("" + location.getLongitude());
            latitudeText.setText("" + location.getLatitude());
//            heightText.setText(""+location.getAltitude()+"米");
//            Poi poi = location.getPoiList().get(0);
//            String poiName = poi.getName();    //获取POI名称
//            String poiAddr = poi.getAddr();    //获取POI地址 //获取周边POI信息
//
//            LogUtil.e("InformationPointActivity",poiName + "\n" + poiAddr);
//            mLocationClient.startIndoorMode();// 开启室内定位模式（重复调用也没问题），开启后，定位SDK会融合各种定位信息（GPS,WI-FI，蓝牙，传感器等）连续平滑的输出定位结果；
//            LogUtil.e("InformationPointActivity",location.getBuildingName() + location.getCity());
//            if(location.getBuildingName() != null){
//                String buildingName = location.getBuildingName();// 百度内部建筑物缩写
//                String floor = location.getFloor();// 室内定位的楼层信息，如 f1,f2,b1,b2
//                LogUtil.e("InformationPointActivity",buildingName + "     " + floor);
//                floorText.setText(location.getFloor());
//            }else{
//                LogUtil.e("InformationPointActivity","null!!!!");
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_information_point);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        MyComponent myComponent = DaggerMyComponent.builder().myModule(new MyModule(this)).build();
        informationPointPresenter = myComponent.informationPointPresenter();

        Intent intent = getIntent();
        //id = intent.getStringExtra("id");
        id = NfcUtil.getID(getIntent());
        TextView idText = findViewById(R.id.ip_id);
        idText.setText(id);

        nameText = findViewById(R.id.ip_name);
        longitudeText = findViewById(R.id.ip_longitude);
        latitudeText = findViewById(R.id.ip_latitude);
        heightText = findViewById(R.id.ip_height);
        floorText = findViewById(R.id.ip_floor);
        //TODO 获取经度纬度
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            requestLocation();
        }

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               informationPointPresenter.register(id, nameText.getText().toString(), longitudeText.getText().toString(),
                       latitudeText.getText().toString(), heightText.getText().toString(), floorText.getText().toString());
            }
        });
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
