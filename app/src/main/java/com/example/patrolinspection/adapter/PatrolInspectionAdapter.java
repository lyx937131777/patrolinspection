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

import com.bumptech.glide.Glide;
import com.example.patrolinspection.PatrolInspectionActivity;
import com.example.patrolinspection.SwipeCardActivity;
import com.example.patrolinspection.db.PatrolInspection;
import com.example.patrolinspection.R;
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class PatrolInspectionAdapter extends RecyclerView.Adapter<PatrolInspectionAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolInspection> mList;

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

    public PatrolInspectionAdapter(List<PatrolInspection> patrolInspectionList)
    {
        mList = patrolInspectionList;
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
                PatrolInspection patrolInspection = mList.get(position);
                Intent intent = new Intent(mContext, SwipeCardActivity.class);
                intent.putExtra("title","用户认证");
                intent.putExtra("type","patrolInspection");
                intent.putExtra("line",((PatrolInspectionActivity)mContext).getIntent().getStringExtra("line"));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PatrolInspectionAdapter.ViewHolder holder, int position)
    {
        PatrolInspection patrolInspection = mList.get(position);
        holder.piStartTime.setText(patrolInspection.getStartTime());
        holder.piEndTime.setText(patrolInspection.getEndTime());
        Glide.with(mContext).load(MapUtil.get(patrolInspection.getState())).into(holder.piState);
        holder.piStateText.setText(patrolInspection.getState());
        holder.piDuringTime.setText(patrolInspection.getDuringTime());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
