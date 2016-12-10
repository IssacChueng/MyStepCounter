package com.issac.mystepcounter.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class Result implements Serializable{
    private String stat;
    private List<News> data;

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public List<News> getData() {
        return data;
    }

    public void setData(List<News> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "stat='" + stat + '\'' +
                ", data=" + data +
                '}';
    }
}
