package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patrolinspection.R;
import com.example.patrolinspection.ScheduleListActivity;
import com.example.patrolinspection.db.PatrolPlan;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolPlan> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView name;
        TextView type;
        TextView startDate;
        TextView endDate;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            type = view.findViewById(R.id.type);
            startDate = view.findViewById(R.id.start_date);
            endDate = view.findViewById(R.id.end_date);
        }
    }

    public PlanListAdapter(List<PatrolPlan> patrolPlanList)
    {
        mList = patrolPlanList;
    }

    @NonNull
    @Override
    public PlanListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_plan_list,parent,false);
        final PlanListAdapter.ViewHolder holder = new PlanListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolPlan patrolPlan = mList.get(position);
                Intent intent = new Intent(mContext, ScheduleListActivity.class);
                intent.putExtra("plan",patrolPlan.getInternetID());
                LogUtil.e("DataUpdatingPlan","发送plan的internetID： " + patrolPlan.getInternetID());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlanListAdapter.ViewHolder holder, int position)
    {
        PatrolPlan patrolPlan = mList.get(position);
        holder.name.setText(patrolPlan.getName());
        holder.type.setText(MapUtil.getPlanType(patrolPlan.getPatrolPlanType()));
        if(patrolPlan.getStartDate() != null){
            holder.startDate.setText(TimeUtil.timeStampToString(patrolPlan.getStartDate(),"yyyy-MM-dd"));
        }else {
            holder.startDate.setText("");
        }
        if(patrolPlan.getEndDate() != null){
            holder.endDate.setText(TimeUtil.timeStampToString(patrolPlan.getEndDate(),"yyyy-MM-dd"));
        }else {
            holder.endDate.setText("");
        }
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
