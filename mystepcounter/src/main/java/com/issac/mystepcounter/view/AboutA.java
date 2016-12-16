package com.issac.mystepcounter.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.issac.mystepcounter.R;

public class AboutA extends AppCompatActivity {
    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViewById(R.id.about_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        content = (TextView) findViewById(R.id.about_content);
        String text = "<a href='https://github.com/IssacChueng/MyStepCounter'><font color = '#787878'>项目地址https://github.com/IssacChueng/MyStepCounter</font></a>";
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            setText(text);
        }else{
            content.setText(Html.fromHtml(text));
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void setText(String text) {
        CharSequence charSequence = Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY);
        content.setText(charSequence);

    }
}
