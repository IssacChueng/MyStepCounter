package com.issac.mystepcounter.utils;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.issac.mystepcounter.AppContext;

/**
 * Created by Administrator on 2016/12/7.
 */

public class MyInputListener implements TextView.OnEditorActionListener {
    TextInputEditText next;

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT){
            next.requestFocus();
            next.setCursorVisible(true);
            Log.i(AppContext.Tag,actionId+":"+v.getImeOptions()+":"+v.getImeActionId()+":"+next.requestFocus());
        }
        return false;
    }

    public void setNext(TextInputEditText editText){
        next = editText;
    }
}
