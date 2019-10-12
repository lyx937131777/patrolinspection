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
        ImageView piState;
        TextView piName;
        TextView piStateText;

        public  ViewHolder(View view)
        {
            super(view);
            piView = view;
            piName = view.findViewById(R.id.pi_name);
            piState = view.findViewById(R.id.pi_state);
            piStateText = view.findViewById(R.id.pi_state_text);
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
                mContext.startActivity(intent);
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
        holder.piName.setText(patrolLine.getPatrolLineName());
//        Glide.with(mContext).load(MapUtil.getState(patrolInspection.getState())).into(holder.piState);
        holder.piStateText.setText(patrolLine.getPatrolLineNo());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
