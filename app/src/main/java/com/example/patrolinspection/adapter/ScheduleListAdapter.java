package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patrolinspection.PointListActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.LogUtil;

import org.litepal.LitePal;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolSchedule> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView name;
        TextView errorRange;
        TextView startTime;
        TextView endTime;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            errorRange = view.findViewById(R.id.error_range);
            startTime = view.findViewById(R.id.start_time);
            endTime = view.findViewById(R.id.end_time);
        }
    }

    public ScheduleListAdapter(List<PatrolSchedule> patrolScheduleList)
    {
        mList = patrolScheduleList;
    }

    @NonNull
    @Override
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_schedule_list,parent,false);
        final ScheduleListAdapter.ViewHolder holder = new ScheduleListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolSchedule patrolSchedule = mList.get(position);
                Intent intent = new Intent(mContext, PointListActivity.class);
                intent.putExtra("line", patrolSchedule.getPatrolLineId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleListAdapter.ViewHolder holder, int position)
    {
        PatrolSchedule patrolSchedule = mList.get(position);
        String lineID = patrolSchedule.getPatrolLineId();
        PatrolLine patrolLine = LitePal.where("internetID = ?",lineID).findFirst(PatrolLine.class);
        holder.name.setText(patrolLine.getPatrolLineName());
        if(patrolSchedule.getPlanType().equals("freeSchedule")){
            holder.errorRange.setText(patrolSchedule.getScheduleName());
        }else{
            holder.errorRange.setText("误差范围："+ patrolSchedule.getErrorRange()+"分钟");
        }
        holder.startTime.setText(patrolSchedule.getStartTime());
        holder.endTime.setText(patrolSchedule.getEndTime());
        LogUtil.e("DataUpdatingSchedule","scheduleId: "+patrolSchedule.getInternetID());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
