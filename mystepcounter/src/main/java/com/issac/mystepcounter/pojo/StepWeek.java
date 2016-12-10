package com.issac.mystepcounter.pojo;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by Administrator on 2016/10/10.
 */

@Table("step_week")
public class StepWeek extends StepValues{
    public static String TABLE = "step_week";
    public StepWeek(long time, int step) {
        super(time, step);
    }
}
