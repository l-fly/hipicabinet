package com.haipai.cabinet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.haipai.cabinet.R;


/**
 * Created by Ashion on 2017/6/15.
 */

public class WaitDialog extends Dialog {
    public WaitDialog(Context context){
        super(context, R.style.transparent_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}
