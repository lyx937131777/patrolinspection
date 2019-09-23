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
import com.example.patrolinspection.DataUpdatingActivity;
import com.example.patrolinspection.R;
import com.example.patrolinspection.SwipeCardActivity;
import com.example.patrolinspection.db.InformationPoint;
import com.example.patrolinspection.db.PatrolInspection;
import com.example.patrolinspection.util.MapUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InformationPointAdapter extends RecyclerView.Adapter<InformationPointAdapter.ViewHolder>
{
    private Context mContext;
    private List<InformationPoint> mList;
    private InformationPoint temppIP;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View ipView;
        ImageView ipState;
        TextView ipStateText;
        TextView ipNum;
        TextView ipTime;
        TextView ipName;

        public  ViewHolder(View view)
        {
            super(view);
            ipView = view;
            ipState = view.findViewById(R.id.ip_state);
            ipStateText = view.findViewById(R.id.ip_state_text);
            ipNum = view.findViewById(R.id.ip_num);
            ipTime = view.findViewById(R.id.ip_time);
            ipName = view.findViewById(R.id.ip_name);
        }
    }

    public InformationPointAdapter(List<InformationPoint> informationPointList)
    {
        mList = informationPointList;
    }

    @NonNull
    @Override
    public InformationPointAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_information_point,parent,false);
        final InformationPointAdapter.ViewHolder holder = new InformationPointAdapter.ViewHolder(view);
        holder.ipView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                InformationPoint informationPoint = mList.get(position);
                if(informationPoint.getState().equals("未巡检")){
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String time =  format.format(calendar.getTime());
                    informationPoint.setTime(time);
                    informationPoint.setState("巡检中");
                    if(temppIP != null){
                        temppIP.setState("已巡检");
                    }
                    temppIP = informationPoint;
                    notifyDataSetChanged();
                }
//                Intent intent = new Intent(mContext, SwipeCardActivity.class);
//                intent.putExtra("title","用户认证");
//                intent.putExtra("type","patrolInspection");
//                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InformationPointAdapter.ViewHolder holder, int position)
    {
        InformationPoint informationPoint = mList.get(position);
        holder.ipName.setText(informationPoint.getName());
        holder.ipNum.setText(""+informationPoint.getNum());
        Glide.with(mContext).load(MapUtil.get(informationPoint.getState())).into(holder.ipState);
        holder.ipStateText.setText(informationPoint.getState());
        holder.ipTime.setText(informationPoint.getTime());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}
