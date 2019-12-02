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
import com.example.patrolinspection.PointRecordListActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.util.Date;
import java.util.List;

public class EventRecordListAdapter extends RecyclerView.Adapter<EventRecordListAdapter.ViewHolder>
{
    private Context mContext;
    private List<EventRecord> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        ImageView state;
        TextView time;
        TextView nameText;
        TextView eventNameText;
        TextView stateText;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            state = view.findViewById(R.id.state);
            time = view.findViewById(R.id.time);
            eventNameText = view.findViewById(R.id.event_name);
            nameText = view.findViewById(R.id.name);
            stateText = view.findViewById(R.id.state_text);
        }
    }

    public EventRecordListAdapter(List<EventRecord> eventRecordList)
    {
        mList = eventRecordList;
    }

    @NonNull
    @Override
    public EventRecordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event_record_list,parent,false);
        final EventRecordListAdapter.ViewHolder holder = new EventRecordListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                EventRecord eventRecord = mList.get(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRecordListAdapter.ViewHolder holder, int position)
    {
        EventRecord eventRecord = mList.get(position);
        Date time = new Date(eventRecord.getTime());
        holder.time.setText(Utility.dateToString(time,"yyyy-MM-dd HH:mm"));
        String state = "未上传";
        if(eventRecord.isUpload()){
            state = "已上传";
        }
        Glide.with(mContext).load(MapUtil.getState(state)).into(holder.state);
        holder.stateText.setText(state);;
        holder.nameText.setText(eventRecord.getPoliceName());
        holder.eventNameText.setText(eventRecord.getEventName());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
