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
import com.example.patrolinspection.db.Notice;
import com.example.patrolinspection.db.PatrolInspection;
import com.example.patrolinspection.util.MapUtil;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>
{
    private Context mContext;
    private List<Notice> mList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View noticeView;
        TextView noticeTitle;
        TextView noticeDate;
        TextView noticeContent;

        public  ViewHolder(View view)
        {
            super(view);
            noticeView = view;
            noticeDate = view.findViewById(R.id.notice_date);
            noticeTitle = view.findViewById(R.id.notice_title);
            noticeContent = view.findViewById(R.id.notice_content);
        }
    }

    public NoticeAdapter(List<Notice> noticeList)
    {
        mList = noticeList;
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notice,parent,false);
        final NoticeAdapter.ViewHolder holder = new NoticeAdapter.ViewHolder(view);
        holder.noticeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, int position)
    {
        Notice notice = mList.get(position);
        holder.noticeDate.setText(notice.getDate());
        holder.noticeTitle.setText(notice.getTitle());
        holder.noticeContent.setText(notice.getContent());
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }
}