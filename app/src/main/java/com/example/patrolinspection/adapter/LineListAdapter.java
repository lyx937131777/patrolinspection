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
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class LineListAdapter extends RecyclerView.Adapter<LineListAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolLine> mList;

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

    public LineListAdapter(List<PatrolLine> patrolLineList)
    {
        mList = patrolLineList;
    }

    @NonNull
    @Override
    public LineListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_line_list,parent,false);
        final LineListAdapter.ViewHolder holder = new LineListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolLine patrolLine = mList.get(position);
                Intent intent = new Intent(mContext, PointListActivity.class);
                intent.putExtra("line",patrolLine.getInternetID());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LineListAdapter.ViewHolder holder, int position)
    {
        PatrolLine patrolLine = mList.get(position);
        holder.name.setText(patrolLine.getPatrolLineName());
        holder.type.setText(MapUtil.getLineType(patrolLine.getPatrolLineType()));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
