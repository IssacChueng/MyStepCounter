package com.issac.mystepcounter;

import android.text.SpannableString;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.issac.mystepcounter.pojo.News;
import com.issac.mystepcounter.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhans on 2016/11/14.
 */

public class NewsAdapter extends BaseAdapter implements ViewHolder.CallBack{
    private LayoutInflater mInflater;
    private List<News> mDatas;
    private List<News> mPreData;
    public NewsAdapter(LayoutInflater inflater){
        this.mInflater =inflater;
        mDatas = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public News getItem(int position) {
        if(position>=0 && position<mDatas.size()){
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        News item = getItem(position);
        int layoutId = getLayoutId(position,item);
        final ViewHolder vh = ViewHolder.getViewHolder(this,convertView,parent,layoutId,position);
        convert(vh,item,position);
        return null;
    }

    public void convert(ViewHolder vh, News item, int position){
        vh.setText(R.id.news_title,item.getTitle());
        vh.setText(R.id.news_source,item.getSource()+" "+item.getDate());

    }

    private int getLayoutId(int position, News item) {
        return R.layout.item_news;
    }


    public LayoutInflater getmInflater() {
        return mInflater;
    }

    @Override
    public RequestManager getRequestManager() {
        return Glide.with(getInflater().getContext());
    }

    @Override
    public LayoutInflater getInflater() {
        return mInflater;
    }
}
