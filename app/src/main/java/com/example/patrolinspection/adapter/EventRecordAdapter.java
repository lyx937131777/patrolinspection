package com.example.patrolinspection.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patrolinspection.EventListActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class EventRecordAdapter extends RecyclerView.Adapter<EventRecordAdapter.ViewHolder>
{
    private Context mContext;
    private List<EventRecord> mList;
    private String type;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView text;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            text = view.findViewById(R.id.text);
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event, parent,false);
        final EventRecordAdapter.ViewHolder holder = new EventRecordAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                EventRecord eventRecord = mList.get(position);
//                if(type.equals("eventType")){
//                    Intent intent = new Intent(mContext, EventListActivity.class);
//                    intent.putExtra("type","eventName");
//                    intent.putExtra("eventType",event.getType());
//                    mContext.startActivity(intent);
//                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRecordAdapter.ViewHolder holder, int position)
    {
        EventRecord eventRecord = mList.get(position);
        if(type.equals("ended")){
            holder.text.setText(eventRecord.getDisposalOperateType());
        }else{
            holder.text.setText("1234567890");
        }

//        if(type.equals("eventType")){
//            holder.text.setText(MapUtil.getEventType(event.getType()));
//        }else{
//            holder.text.setText(event.getName());
//        }

    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
