package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.DataUpdatingActivity;
import com.example.patrolinspection.InformationRegisterActivity;
import com.example.patrolinspection.NfcActivity;
import com.example.patrolinspection.NoticeActivity;
import com.example.patrolinspection.PatrolLineActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SchoolEventActivity;
import com.example.patrolinspection.SignActivity;
import com.example.patrolinspection.SwipeCardActivity;
import com.example.patrolinspection.SwipeNfcActivity;
import com.example.patrolinspection.SystemParameterActivity;
import com.example.patrolinspection.EventRecordActivity;
import com.example.patrolinspection.TestActivity;
import com.example.patrolinspection.db.Type;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder>
{
    private Context mContext;
    private List<Type> mTypeList;
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView typeImage;
//        TextView typenName;

        public  ViewHolder(View view)
        {
            super(view);
            cardView = (CardView) view;
            typeImage = (ImageView) view.findViewById(R.id.type_image);
//            typenName = (TextView) view.findViewById(R.id.type_name);
        }
    }

    public TypeAdapter(List<Type> typeList)
    {
        mTypeList = typeList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_type,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                LogUtil.e("TypeAdapter","TouchListener 触发" + motionEvent.getAction());
                int position = holder.getAdapterPosition();
                Type type = mTypeList.get(position);
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Glide.with(mContext).load(type.getImagePressID()).into(holder.typeImage);
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                    Glide.with(mContext).load(type.getImageID()).into(holder.typeImage);
                }
                return false;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Type type = mTypeList.get(position);
                String typeName = type.getTypeName();
                switch (typeName){
                    case "patrolInspection":{
                        Intent intent = new Intent(mContext, PatrolLineActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    case "sign":{
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                        if(pref.getString("equipmentType",null).equals("phone")&&(!pref.getBoolean("isAppAttendance",false))){
                            Toast.makeText(mContext,"手机无法签到，请用巡更棒进行签到！",Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent = new Intent(mContext, SignActivity.class);
                            mContext.startActivity(intent);
                        }
                        break;
                    }
                    case "signIn":
                    case "signOut":{
                        Intent intent = new Intent(mContext, SwipeNfcActivity.class);
                        intent.putExtra("type",typeName);
                        intent.putExtra("title", MapUtil.getFaceType(typeName));
                        intent.putExtra("attendanceType",type.getName());
                        mContext.startActivity(intent);
                        break;
                    }
                    case "notice":{
                        Intent intent = new Intent(mContext, NoticeActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    case "eventFound":{
                        Intent intent = new Intent(mContext, SwipeNfcActivity.class);
                        intent.putExtra("type",typeName);
                        intent.putExtra("title",type.getName());
                        mContext.startActivity(intent);
                        break;
                    }
                    case "eventList":{
                        Intent intent = new Intent(mContext, EventRecordActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    case "dataUpdating":{
                        Intent intent = new Intent(mContext, DataUpdatingActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    case "securityStaff":{
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                        if(pref.getString("equipmentType",null).equals("phone")){
                            Toast.makeText(mContext,"手机无法注册保安，请使用巡更棒注册！",Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(mContext, SwipeCardActivity.class);
                            intent.putExtra("type",typeName);
                            intent.putExtra("title",type.getName());
                            mContext.startActivity(intent);
                        }
                        break;
                    }
                    case "informationPoint":{
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                        if(pref.getString("equipmentType",null).equals("phone")){
                            Toast.makeText(mContext,"手机无法注册信息点，请使用巡更棒注册！",Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent(mContext, NfcActivity.class);
                            intent.putExtra("type",typeName);
                            intent.putExtra("title",type.getName());
                            mContext.startActivity(intent);
                        }
                        break;
                    }
                    case "systemParameter":{
                        Intent intent = new Intent(mContext, SystemParameterActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    case "schoolEvent":{
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
                        if(pref.getString("schoolPolice","null").equals("null")){
                            Intent intent = new Intent(mContext, SwipeNfcActivity.class);
                            intent.putExtra("type",typeName);
                            intent.putExtra("title","护校登录");
                            mContext.startActivity(intent);
                        }else{
                            Intent intent = new Intent(mContext, SchoolEventActivity.class);
                            mContext.startActivity(intent);
                        }

                        break;
                    }
                    case "informationRegister":{
                        Intent intent = new Intent(mContext, InformationRegisterActivity.class);
                        mContext.startActivity(intent);
                        break;
                    }
                    default:
                        LogUtil.e("TypeAdapter","错误类型！！！");
                        break;
                }

//                if(mposition == 0)
//                {
//                    Intent intent = new Intent(mContext,SearchActivity.class);
//                    intent.putExtra("type",type.getTypeName());
//                    mContext.startActivity(intent);
//                }else
//                {
//                    MainActivity.setCommond(3);
//                    CompareActivity compareActivity = (CompareActivity)mContext;
//                    LogUtil.e("TypeAdapter","hahahhahahahahahha"+ String.valueOf(mposition));
//                    Intent intent = new Intent(compareActivity,SearchActivity.class);
//                    intent.putExtra("type",type.getTypeName());
//                    compareActivity.startActivityForResult(intent,mposition);
//                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Type type = mTypeList.get(position);
//        holder.typenName.setText(type.getName());
        Glide.with(mContext).load(type.getImageID()).into(holder.typeImage);
    }

    @Override
    public int getItemCount()
    {
        return mTypeList.size();
    }
}
