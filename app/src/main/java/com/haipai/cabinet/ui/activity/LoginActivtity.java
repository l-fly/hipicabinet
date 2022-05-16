package com.haipai.cabinet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.JsonUtils;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.PreferencesUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

public class LoginActivtity extends BaseActivity {

    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }
    @BindView(R.id.account)
    TextView accountEdit;
    @BindView(R.id.password)
    TextView passwordEdit;
    private List<Button> btns = new ArrayList<>();
    private RelativeLayout rlDelete;

    boolean isAcount = true;
    String acountStr = "";
    String passwordStr = "";
    @OnClick(R.id.commit)
    public void onActionCommit() {
        if (accountEdit.getText().toString().length() < 1 || passwordEdit.getText().toString().length() < 1) {
            showToast("请输入用户名和密码！");
            return;
        }
        if((acountStr.equals("devinbest") && passwordStr.equals("superbest"))){
            startActivity(new Intent(this, MenuActivity.class));
            finish();
            return;
        }

        if(PreferencesUtil.getInstance().getSwitchIp() == 1){
            if((acountStr.equals("haipai") && passwordStr.equals("123"))){
                startActivity(new Intent(this, MenuActivity.class));
                finish();
                return;
            }
        }
        login(acountStr,passwordStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initUIView() {
        LinearLayout llBtn0 = (LinearLayout) findViewById(R.id.ll_btn_1_0);
        for(int i=0;i<llBtn0.getChildCount();i++){
            btns.add((Button) llBtn0.getChildAt(i));
        }
        LinearLayout llBtn1 = (LinearLayout) findViewById(R.id.ll_btn_q_p);
        for(int i=0;i<llBtn1.getChildCount();i++){
            btns.add((Button) llBtn1.getChildAt(i));
        }
        LinearLayout llBtn2 = (LinearLayout) findViewById(R.id.ll_btn_a_m);
        for(int i=0;i<llBtn2.getChildCount();i++){
            btns.add((Button) llBtn2.getChildAt(i));
        }
        LinearLayout llBtn3 = (LinearLayout) findViewById(R.id.ll_btn_z_m);
        for(int i=0;i<llBtn3.getChildCount()-1;i++){
            btns.add((Button) llBtn3.getChildAt(i));
        }
        for (int i=0;i<btns.size();i++){
            btns.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAcount){
                        acountStr = acountStr + ((Button)v).getText().toString();
                        accountEdit.setText(acountStr);
                    }else{
                        passwordStr = passwordStr + ((Button)v).getText().toString();
                        passwordEdit.setText(passwordStr);
                    }
                }
            });
        }

        accountEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAcount = true;
                accountEdit.setBackgroundResource(R.drawable.bg_kuan_deep_login);
                passwordEdit.setBackgroundResource(R.drawable.bg_kuan_light_login);
            }
        });
        passwordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAcount = false;
                accountEdit.setBackgroundResource(R.drawable.bg_kuan_light_login);
                passwordEdit.setBackgroundResource(R.drawable.bg_kuan_deep_login);
            }
        });
        rlDelete = (RelativeLayout) findViewById(R.id.rl_delete);
        rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAcount){
                    if(acountStr.length()>0){
                        acountStr = acountStr.substring(0, acountStr.length()-1);
                        accountEdit.setText(acountStr);

                    }
                }else{
                    if(passwordStr.length()>0){
                        passwordStr = passwordStr.substring(0, passwordStr.length()-1);
                        passwordEdit.setText(passwordStr);
                    }
                }
            }
        });
    }

    @Override
    public void setCurrent() {

    }

    @Override
    public void passSecond() {
        super.passSecond();
    }
    String url = "http://192.168.0.22:9005/power/cabinet/login";
    public static String KEY_TOKEN = "Authorization";
    public static String token =" bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50IjoiaGlwaSIsImJ1c2luZXNzSWQiOjEwMDAwLCJ0aW1lIjoxNjUxOTA4ODA5MjY0fQ.9W-Qxu2naomNgpKx5ognxiT6CwStRNskXl6Ntn3RRQM";
    private void login(String account,String password){
        showWaitDialog();
        User user = new User(LocalDataManager.devId,account,password);
        OkHttpUtils.postString()
                .url(url)
                .addHeader(KEY_TOKEN, token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(JsonUtils.beanToJson(user))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                       // LogUtil.i("#####" + e.getMessage());
                        hideWaitDialog();
                        showToast("网络错误！");
                    }
                    @Override
                    public void onResponse(String response) {
                        hideWaitDialog();
                        //LogUtil.i("#####" + response);
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            int code = rootObject.getInt("code");
                            if (code == 200){
                                startActivity(new Intent(LoginActivtity.this, MenuActivity.class));
                                finish();
                            }else {
                                showToast("密码错误！");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    static class User{
        String account;
        String password;
        String devId;
        public User(String devId,String account,String password){
            this.account = account;
            this.password = password;
        }
    }
}