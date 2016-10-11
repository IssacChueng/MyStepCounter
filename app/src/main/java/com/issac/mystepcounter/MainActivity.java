package com.issac.mystepcounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.issac.mystepcounter.pojo.StepData;
import com.litesuits.orm.LiteOrm;

public class MainActivity extends AppCompatActivity {

    static LiteOrm liteOrm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (liteOrm == null){
            liteOrm = LiteOrm.newCascadeInstance(this,"step.db");
            liteOrm.setDebugged(true);
        }
        StepData stepData = new StepData();
        stepData.setStep("2");
        stepData.setToday("today");
        liteOrm.insert(stepData);
    }

}
