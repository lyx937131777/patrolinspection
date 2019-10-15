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
import com.example.patrolinspection.db.Police;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.util.List;

public class PoliceListAdapter extends RecyclerView.Adapter<PoliceListAdapter.ViewHolder>
{
    private Context mContext;
    private List<Police> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView name;
        TextView type;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            type = view.findViewById(R.id.type);
        }
    }

    public PoliceListAdapter(List<Police> policeList)
    {
        mList = policeList;
    }

    @NonNull
    @Override
    public PoliceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_police_list,parent,false);
        final PoliceListAdapter.ViewHolder holder = new PoliceListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Police police = mList.get(position);
//                Intent intent = new Intent(mContext, ScheduleListActivity.class);
//                intent.putExtra("plan",patrolPlan.getInternetID());
//                LogUtil.e("DataUpdatingPlan","发送plan的internetID： " + patrolPlan.getInternetID());
//                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PoliceListAdapter.ViewHolder holder, int position)
    {
        Police police = mList.get(position);
        holder.name.setText(police.getRealName());
        if(police.isOfficialPolice()){
            holder.type.setText("保安卡");
        }else{
            holder.type.setText("IC卡");
        }
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
