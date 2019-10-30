package com.example.patrolinspection.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.patrolinspection.R;
import com.example.patrolinspection.db.EventRecord;
import com.example.patrolinspection.db.HandleRecord;
import com.example.patrolinspection.util.HttpUtil;
import com.example.patrolinspection.util.LogUtil;
import com.example.patrolinspection.util.MapUtil;
import com.example.patrolinspection.util.Utility;

import java.util.List;

public class HandleRecordAdapter extends RecyclerView.Adapter<HandleRecordAdapter.ViewHolder>
{
    private Context mContext;
    private List<HandleRecord> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        TextView nameText;
        TextView detailText;
        TextView typeText;
        TextView timeText;
        ImageView photo;

        public  ViewHolder(View view)
        {
            super(view);
            this.view = view;
            nameText = view.findViewById(R.id.name);
            detailText = view.findViewById(R.id.detail);
            typeText = view.findViewById(R.id.type);
            timeText = view.findViewById(R.id.time);
            photo = view.findViewById(R.id.photo);
        }
    }

    public HandleRecordAdapter(List<HandleRecord> handleRecordList)
    {
        mList = handleRecordList;
    }

    @NonNull
    @Override
    public HandleRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_handle_record, parent,false);
        final HandleRecordAdapter.ViewHolder holder = new HandleRecordAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                HandleRecord handleRecord = mList.get(position);
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
    public void onBindViewHolder(@NonNull HandleRecordAdapter.ViewHolder holder, int position)
    {
        HandleRecord handleRecord = mList.get(position);
        holder.nameText.setText(handleRecord.getPoliceName());
        holder.detailText.setText(handleRecord.getDetail());
        holder.typeText.setText(MapUtil.getHandleType(handleRecord.getDisposalOperateType()));
        holder.timeText.setText(Utility.dateStringToString(handleRecord.getOperateTime(),"yyyy-MM-dd HH:mm"));
        Glide.with(mContext).load(HttpUtil.getPhotoURL(handleRecord.getPhoto())).into(holder.photo);
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
