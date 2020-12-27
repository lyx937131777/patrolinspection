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
import com.example.patrolinspection.EventListActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.Event;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.util.MapUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

//显示事件的适配器
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>
{
    private Context mContext;
    private List<Event> mList;
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

    public EventAdapter(List<Event> eventList, String type)
    {
        mList = eventList;
        this.type = type;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event, parent,false);
        final EventAdapter.ViewHolder holder = new EventAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Event event = mList.get(position);
                if(type.equals("eventType")){
                    Intent intent = new Intent(mContext, EventListActivity.class);
                    intent.putExtra("type","eventName");
                    intent.putExtra("eventType",event.getType());
                    mContext.startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position)
    {
        Event event = mList.get(position);
        if(type.equals("eventType")){
            holder.text.setText(MapUtil.getEventType(event.getType()));
        }else{
            holder.text.setText(event.getName());
        }

    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
