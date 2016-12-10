package com.issac.mystepcounter.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.issac.mystepcounter.R;
import com.issac.mystepcounter.pojo.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<News> newses;

    public RecyclerAdapter(Context context,List<News> newses) {
        this.mContext = context;
        this.newses = newses;
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    private OnItemClickListener listener;

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new RecyclerAdapter.MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_news, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.MyViewHolder holder, int position) {
        holder.position = position;
        News news = newses.get(position);
        holder.tv_title.setText(news.getTitle());
        holder.tv_source.setText(news.getDate());
        Glide.with(mContext).load(news.getThumbnail_pic_s()).into(holder.img_news);

    }

    @Override
    public int getItemCount() {
        //// TODO: 2016/11/11 lll
        return newses.size();
    }

    public void add(List<News> newses){
        checkIfNull();
        clean();
        for (News news : newses){
            this.newses.add(news);
        }
        notifyDataSetChanged();
    }

    private void checkIfNull() {
        if (this.newses == null){
            newses = new ArrayList<>();
        }
    }

    public void clean(){
        this.newses.clear();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_title;
        public TextView tv_source;
        public ImageView img_news;
        public int position;

        public MyViewHolder(View view) {
            super(view);
            //tv = (TextView) view.findViewById(R.id.item_name);
            tv_title = (TextView) view.findViewById(R.id.news_title);
            tv_source = (TextView) view.findViewById(R.id.news_source);
            img_news = (ImageView) view.findViewById(R.id.news_img);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("async","position="+position);
            if (null!= listener){
                listener.onClick(position);
            }
        }
    }
}
