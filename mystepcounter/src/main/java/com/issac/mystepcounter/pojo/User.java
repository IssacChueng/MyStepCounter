package com.issac.mystepcounter.pojo;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/11/23.
 */

public class User extends BmobUser {
    private String weight;
    private String height;
    private BmobFile avatar;
    private String sex;
    private Integer stepCount;



    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    /**
     * 全部设置
     *
     * @param avatar 头像
     * @param weight 体重
     * @param height 身高
     * @param sex 性别
     * @param email 邮箱
     */
    public void set(BmobFile avatar, String weight, String height, String sex, String email,Integer stepCount){
        this.avatar = avatar;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.setEmail(email);
        this.stepCount = stepCount;

    }

    public Integer getStepCount() {
        return stepCount;
    }

    public void setStepCount(Integer stepCount) {
        this.stepCount = stepCount;
    }
}
