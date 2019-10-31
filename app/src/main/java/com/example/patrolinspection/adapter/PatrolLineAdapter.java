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

import com.example.patrolinspection.PatrolInspectionActivity;
import com.example.patrolinspection.PatrolLineActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.db.PatrolSchedule;

import org.litepal.LitePal;

import java.util.List;

public class PatrolLineAdapter extends RecyclerView.Adapter<PatrolLineAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolSchedule> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View piView;
        TextView plName;

        public  ViewHolder(View view)
        {
            super(view);
            piView = view;
            plName = view.findViewById(R.id.pl_name);
        }
    }

    public PatrolLineAdapter(List<PatrolSchedule> patrolScheduleList)
    {
        mList = patrolScheduleList;
    }

    @NonNull
    @Override
    public PatrolLineAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_patrol_line,parent,false);
        final PatrolLineAdapter.ViewHolder holder = new PatrolLineAdapter.ViewHolder(view);
        holder.piView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolSchedule patrolSchedule = mList.get(position);
                Intent intent = new Intent(mContext, PatrolInspectionActivity.class);
                intent.putExtra("line",patrolSchedule.getPatrolLineId());
                intent.putExtra("plan",patrolSchedule.getPatrolPlanId());
                ((PatrolLineActivity)mContext).startActivityForResult(intent,0);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatrolLineAdapter.ViewHolder holder, int position)
    {
        PatrolSchedule patrolSchedule = mList.get(position);
        String lineID = patrolSchedule.getPatrolLineId();
        PatrolLine patrolLine = LitePal.where("internetId = ?",lineID).findFirst(PatrolLine.class);
        holder.plName.setText(patrolLine.getPatrolLineName());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
