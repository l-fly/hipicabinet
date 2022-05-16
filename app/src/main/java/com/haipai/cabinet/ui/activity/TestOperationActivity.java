package com.haipai.cabinet.ui.activity;



import android.os.Bundle;

import com.haipai.cabinet.R;

import butterknife.OnClick;

public class TestOperationActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_operation);
    }

    @Override
    public void initUIView() {

    }
}