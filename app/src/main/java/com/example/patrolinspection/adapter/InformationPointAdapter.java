package com.example.patrolinspection.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.PatrolingActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolPointRecord;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class InformationPointAdapter extends RecyclerView.Adapter<InformationPointAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolPointRecord> mList;
    private PatrolPointRecord tempPointRecord;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View ipView;
        ImageView ipState;
        TextView ipStateText;
        TextView ipNum;
        TextView ipTime;
        TextView ipName;

        public  ViewHolder(View view)
        {
            super(view);
            ipView = view;
            ipState = view.findViewById(R.id.ip_state);
            ipStateText = view.findViewById(R.id.ip_state_text);
            ipNum = view.findViewById(R.id.ip_num);
            ipTime = view.findViewById(R.id.ip_time);
            ipName = view.findViewById(R.id.ip_name);
        }
    }

    public InformationPointAdapter(List<PatrolPointRecord> patrolPointRecordList)
    {
        mList = patrolPointRecordList;
        Collections.sort(mList, new Comparator<PatrolPointRecord>()
        {
            @Override
            public int compare(PatrolPointRecord o1, PatrolPointRecord o2)
            {
                return Integer.parseInt(o1.getOrderNo())-Integer.parseInt(o2.getOrderNo());
            }
        });
    }

    @NonNull
    @Override
    public InformationPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_information_point,parent,false);
        final InformationPointAdapter.ViewHolder holder = new InformationPointAdapter.ViewHolder(view);
//        holder.ipView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                int position = holder.getAdapterPosition();
//                PatrolPointRecord patrolPointRecord = mList.get(position);
//                if(patrolPointRecord.getState().equals("未巡检")){
//                    Calendar calendar = Calendar.getInstance();
//                    patrolPointRecord.setTime(calendar.getTimeInMillis());
//                    patrolPointRecord.setState("巡检中");
//                    patrolPointRecord.save();
//                    if(tempPointRecord != null){
//                        tempPointRecord.setState("已巡检");
//                        tempPointRecord.save();
//                    }
//                    tempPointRecord = patrolPointRecord;
//                    ((PatrolingActivity)mContext).addCount();
//                    notifyDataSetChanged();
//                }
//            }
//        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InformationPointAdapter.ViewHolder holder, int position)
    {
        PatrolPointRecord patrolPointRecord = mList.get(position);
        holder.ipName.setText(patrolPointRecord.getPointName());
        holder.ipNum.setText(patrolPointRecord.getOrderNo());
        Glide.with(mContext).load(MapUtil.getState(patrolPointRecord.getState())).into(holder.ipState);
        holder.ipStateText.setText(patrolPointRecord.getState());
        long time = patrolPointRecord.getTime();
        if(time != 0){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            holder.ipTime.setText(format.format(new Date(time)));
        }else {
            holder.ipTime.setText("");
        }
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
