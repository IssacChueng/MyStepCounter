package com.issac.mystepcounter;

import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.issac.mystepcounter.utils.ImageLoader;

/**
 * Created by zhans on 2016/11/14.
 */

public class ViewHolder {
    private SparseArray<View> mViews;
    private int mLayoutId;
    private View mConvertView;
    private int mPosition;
    private LayoutInflater inflater;
    private RequestManager requestManager;
    private CallBack callBack;

    public interface CallBack{
        RequestManager getRequestManager();
        LayoutInflater getInflater();
    }

    public ViewHolder(CallBack callBack, ViewGroup parent, int layoutId, int position){
        this.callBack = callBack;
        this.mViews = new SparseArray<>();
        this.mPosition = position;
        this.mLayoutId = layoutId;
        inflater = callBack.getInflater();
        this.mConvertView = inflater.inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder getViewHolder(NewsAdapter adapter, View convertView, ViewGroup parent, int layoutId, int position){
        boolean needCreateView = false;
        ViewHolder vh = null;
        if (convertView == null) {
            needCreateView = true;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (vh != null && (vh.mLayoutId != layoutId)) {
            needCreateView = true;
        }
        if (needCreateView) {
            return new ViewHolder(adapter, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    // 通过一个viewId来获取一个view
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return this.mConvertView;
    }

    // 给TextView设置文字
    public void setText(int viewId, String text) {
        if (TextUtils.isEmpty(text)) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    // 给TextView设置文字
    public void setText(int viewId, SpannableString text) {
        if (text == null) return;
        TextView tv = getView(viewId);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    public void setTextColor(int viewId, int textColor) {
        TextView tv = getView(viewId);
        tv.setTextColor(textColor);
        tv.setVisibility(View.VISIBLE);
    }

    public void setImageForNet(int viewId, String imgUrl, int emptyRes) {
        ImageView iv = getView(viewId);
        RequestManager loader = callBack.getRequestManager();
        ImageLoader.loadImage(loader, iv, imgUrl, emptyRes);
    }
}
