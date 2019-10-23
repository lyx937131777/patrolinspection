package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SwipeNfcActivity;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class PatrolInspectionAdapter extends RecyclerView.Adapter<PatrolInspectionAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolSchedule> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View piView;
        ImageView piState;
        TextView piStartTime;
        TextView piEndTime;
        TextView piDuringTime;
        TextView piStateText;

        public  ViewHolder(View view)
        {
            super(view);
            piView = view;
            piState = view.findViewById(R.id.pi_state);
            piStartTime = view.findViewById(R.id.pi_start_time);
            piEndTime = view.findViewById(R.id.pi_end_time);
            piDuringTime = view.findViewById(R.id.pi_during_time);
            piStateText = view.findViewById(R.id.pi_state_text);
        }
    }

    public PatrolInspectionAdapter(List<PatrolSchedule> patrolScheduleList)
    {
        mList = patrolScheduleList;
    }

    @NonNull
    @Override
    public PatrolInspectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_patrol_inspection,parent,false);
        final PatrolInspectionAdapter.ViewHolder holder = new PatrolInspectionAdapter.ViewHolder(view);
        holder.piView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolSchedule patrolSchedule = mList.get(position);
                if(System.currentTimeMillis() < patrolSchedule.getEndLimit()){
                    Intent intent = new Intent(mContext, SwipeNfcActivity.class);
                    intent.putExtra("title","用户认证");
                    intent.putExtra("type","patrolInspection");
                    intent.putExtra("schedule",patrolSchedule.getInternetID());
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext,"该巡检已结束，无法巡检",Toast.LENGTH_LONG).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatrolInspectionAdapter.ViewHolder holder, int position)
    {
        PatrolSchedule patrolSchedule = mList.get(position);
        holder.piStartTime.setText(patrolSchedule.getStartTime());
        holder.piEndTime.setText(patrolSchedule.getEndTime());
        long time = System.currentTimeMillis();
        String state = null;
        if(time < patrolSchedule.getStartTimeHead()){
            state = "未开始";
        }else if(time < patrolSchedule.getEndLimit()){
            state = "进行中";
        }else{
            state = "已结束";
        }
        Glide.with(mContext).load(MapUtil.getState(state)).into(holder.piState);
        holder.piStateText.setText(state);
        holder.piDuringTime.setText(patrolSchedule.getDuringMin()+"分钟");
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
