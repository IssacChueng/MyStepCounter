package com.issac.mystepcounter.pojo;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by Administrator on 2016/10/11.
 */

public class StepValues {
    public static String TABLE = "";
    public static String TIME = "time";
    public static String STEP = "step";
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    @Column("time")
    private long time;
    @Column("step")
    private int step;

    public StepValues() {
    }

    public StepValues(long time, int step) {
        this.time = time;
        this.step = step;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
