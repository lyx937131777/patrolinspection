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
import com.example.patrolinspection.SchoolEventHandleActivity;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.SchoolEventRecord;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.util.List;

public class SchoolEventRecordAdapter extends RecyclerView.Adapter<SchoolEventRecordAdapter.ViewHolder>
{
    private Context mContext;
    private List<SchoolEventRecord> mList;
    private String type;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView typeText;
        TextView stateText;
        TextView timeText;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            typeText = view.findViewById(R.id.type);
            stateText = view.findViewById(R.id.state);
            timeText = view.findViewById(R.id.time);
        }
    }

    public SchoolEventRecordAdapter(List<SchoolEventRecord> schoolEventRecordList, String type)
    {
        mList = schoolEventRecordList;
        this.type = type;
    }

    @NonNull
    @Override
    public SchoolEventRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_school_event, parent,false);
        final SchoolEventRecordAdapter.ViewHolder holder = new SchoolEventRecordAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                SchoolEventRecord schoolEventRecord = mList.get(position);
                Intent intent = new Intent(mContext, SchoolEventHandleActivity.class);
                intent.putExtra("schoolEventRecord",schoolEventRecord);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolEventRecordAdapter.ViewHolder holder, int position)
    {
        SchoolEventRecord schoolEventRecord = mList.get(position);
        holder.typeText.setText(MapUtil.getSchoolEventType(schoolEventRecord.getSchoolEventType()));
        holder.stateText.setText(schoolEventRecord.getState());
        holder.timeText.setText(Utility.dateStringToString(schoolEventRecord.getOccurrenceTime(),"yyyy-MM-dd HH:mm"));
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
