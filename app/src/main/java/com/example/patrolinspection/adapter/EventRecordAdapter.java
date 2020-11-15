package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patrolinspection.HandleRecordActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import java.util.List;

public class EventRecordAdapter extends RecyclerView.Adapter<EventRecordAdapter.ViewHolder>
{
    private Context mContext;
    private List<EventRecord> mList;
    private String type;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView nameText;
        TextView detailText;
        TextView timeText;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            nameText = view.findViewById(R.id.name);
            detailText = view.findViewById(R.id.detail);
            timeText = view.findViewById(R.id.time);
        }
    }

    public EventRecordAdapter(List<EventRecord> eventRecordList, String type)
    {
        mList = eventRecordList;
        this.type = type;
    }

    @NonNull
    @Override
    public EventRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event_record, parent,false);
        final EventRecordAdapter.ViewHolder holder = new EventRecordAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                EventRecord eventRecord = mList.get(position);
                Intent intent = new Intent(mContext, HandleRecordActivity.class);
                intent.putExtra("eventRecord",eventRecord.getInternetID());
                intent.putExtra("type",type);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRecordAdapter.ViewHolder holder, int position)
    {
        EventRecord eventRecord = mList.get(position);
        holder.nameText.setText(eventRecord.getPoliceName());
        holder.detailText.setText(eventRecord.getEventName());
        holder.timeText.setText(TimeUtil.timeStampToString(eventRecord.getLastTime(),"yyyy-MM-dd HH:mm"));
//        if(type.equals("ended")){
//
//        }else{
//            holder.text.setText("1234567890");
//        }

    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
