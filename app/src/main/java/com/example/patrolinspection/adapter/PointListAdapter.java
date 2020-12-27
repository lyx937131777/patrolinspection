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
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolIP;
import com.example.patrolinspection.db.PatrolLine;
import com.example.patrolinspection.util.MapUtil;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//显示信息点（数据更新界面）的适配器
public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolIP> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView name;
        TextView num;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.name);
            num = view.findViewById(R.id.num);
        }
    }

    public PointListAdapter(List<PatrolIP> patrolIPList)
    {
        mList = patrolIPList;
    }

    @NonNull
    @Override
    public PointListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_point_list,parent,false);
        final PointListAdapter.ViewHolder holder = new PointListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointListAdapter.ViewHolder holder, int position)
    {
        PatrolIP patrolIP = mList.get(position);
        holder.name.setText(patrolIP.getPointName());
        holder.num.setText(patrolIP.getPointNo());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
