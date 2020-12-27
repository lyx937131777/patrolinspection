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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.PatrolInspectionActivity;
import com.example.patrolinspection.PointRecordListActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SwipeNfcActivity;
import com.example.patrolinspection.db.PatrolRecord;
import com.example.patrolinspection.db.PatrolSchedule;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.TimeUtil;
import com.example.patrolinspection.util.Utility;

import java.util.Date;
import java.util.List;

//显示上传列表的适配器
public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>
{
    private Context mContext;
    private List<PatrolRecord> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        ImageView state;
        TextView startTime;
        TextView endTime;
        TextView nameText;
        TextView stateText;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            state = view.findViewById(R.id.state);
            startTime = view.findViewById(R.id.start_time);
            endTime = view.findViewById(R.id.end_time);
            nameText = view.findViewById(R.id.name);
            stateText = view.findViewById(R.id.state_text);
        }
    }

    public UploadListAdapter(List<PatrolRecord> patrolRecordList)
    {
        mList = patrolRecordList;
    }

    @NonNull
    @Override
    public UploadListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_upload_list,parent,false);
        final UploadListAdapter.ViewHolder holder = new UploadListAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                PatrolRecord patrolRecord = mList.get(position);
                Intent intent = new Intent(mContext, PointRecordListActivity.class);
                intent.putExtra("record",patrolRecord.getInternetID());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadListAdapter.ViewHolder holder, int position)
    {
        PatrolRecord patrolRecord = mList.get(position);
        Date startTime = new Date(patrolRecord.getStartTimeLong());
        Date endTime = new Date(patrolRecord.getEndTime());
        holder.startTime.setText(TimeUtil.dateToString(startTime,"yyyy-MM-dd HH:mm"));
        holder.endTime.setText(TimeUtil.dateToString(endTime,"yyyy-MM-dd HH:mm"));
        String state = "未上传";
        if(patrolRecord.isUpload()){
            state = "已上传";
        }
        Glide.with(mContext).load(MapUtil.getState(state)).into(holder.state);
        holder.stateText.setText(state);;
        holder.nameText.setText(patrolRecord.getLineName());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
