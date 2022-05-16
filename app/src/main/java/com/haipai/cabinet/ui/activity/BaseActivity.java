package com.haipai.cabinet.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.haipai.cabinet.MyApplication;
import com.haipai.cabinet.R;
import com.haipai.cabinet.manager.LocalDataManager;
import com.haipai.cabinet.ui.dialog.WaitDialog;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;
import com.haipai.cabinet.util.PreferencesUtil;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public PreferencesUtil mPreferences;
    private long lastActive = 0;
    private long maxStayDuration = 180*1000; //最大停留时间，默认3分钟
    private long mSpeakTime = 0;
    private long mShowToast = 0;
    private boolean isStop = false;
    long firstTouch = 0;   //连续触摸事件down的时间
    long lastTouch = 0;    //连续触摸事件最后一次move或者up的时间
    MyApplication.SecondCheck baseSecondCheck = new MyApplication.SecondCheck() {
        @Override
        public void onPassSecond() {
            if (!BaseActivity.this.getLocalClassName().contains("MainActivity") && !isStop && CustomMethodUtil.elapsedRealtime() - lastActive > maxStayDuration) {
                if (CustomMethodUtil.elapsedRealtime() - lastActive > 24 * 3600000) {
                    lastActive = CustomMethodUtil.elapsedRealtime();
                    return;
                }
                isStop = true;
                finish();
                return;
            }
            if (isStop) {
                return;
            }
            long now = CustomMethodUtil.elapsedRealtime();
            //若down事件跟up事件时间一样，说明并没有up，或者up事件与down事件间隔超过一定时间，若保持状态超过一定时间，说明触屏失效了,<3600是防止时间跳动误报
            if ((firstTouch == lastTouch && firstTouch != 0 && now - firstTouch > 60000 && now - firstTouch < 3600000)
                    || (lastTouch - firstTouch > 60000 && lastTouch - firstTouch < 3600000)) {
                firstTouch = now + 300000;
                LogUtil.f("认为触摸失效重启，firstTouch=" + firstTouch + ",lastTouch=" + lastTouch + ",now=" + now);
                /**
                 * todo
                 * 需要重启屏幕
                 */
                //LocalDataManager.resetStartTime = now;
                //LocalDataManager.requestReset = 30;
            }

            passSecond();
        }
    };
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        long now = CustomMethodUtil.elapsedRealtime();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            firstTouch = now;
            lastTouch = now;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE || ev.getAction() == MotionEvent.ACTION_UP) {
            //避免认为没有up/move事件
            if (now == firstTouch) {
                now++;
            }
            lastTouch = now;
        }
        return super.dispatchTouchEvent(ev);
    }
    public void passSecond() {
    }
    public void setLastActive() {
        lastActive = CustomMethodUtil.elapsedRealtime();
    }

    public void setStayDuration(long duration) {
        maxStayDuration = duration;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPreferences = PreferencesUtil.getInstance(this);
    }
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initUIView();
    }
    public abstract void initUIView();

    public void setCurrent(){
        LocalDataManager.currentActivity = LocalDataManager.OTHER_ACTIVITY;
    };
    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        lastActive = CustomMethodUtil.elapsedRealtime();
        ((MyApplication) getApplication()).setSecondCheck(baseSecondCheck);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setCurrent();
    }
    @Override
    protected void onStop() {
        isStop = true;
        ((MyApplication) getApplication()).removeSecondCheck(baseSecondCheck);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean isStop() {
        return isStop;
    }
    /**
     * 隐藏键盘
     */
    public void hideSoftKeyboard() {
        try {
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){}
    }

    /**
     * 吐司
     * @param text
     */
    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(CustomMethodUtil.elapsedRealtime() - mShowToast>1500) {
                    mShowToast = CustomMethodUtil.elapsedRealtime();
                    View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(R.layout.toast_layout, null);
                    ((TextView) toastRoot.findViewById(R.id.text)).setText(text);
                    Toast toast = new Toast(BaseActivity.this);
                    //获取屏幕高度  
                    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int height = wm.getDefaultDisplay().getHeight();
                    //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
                    toast.setGravity(Gravity.TOP, 0, height * 1 / 2);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastRoot);
                    toast.show();
                }
            }
        });

    }
    public boolean speak(String msg) {
        if (CustomMethodUtil.elapsedRealtime() - mSpeakTime < 2000) {
            return false;
        }else {
            mSpeakTime = CustomMethodUtil.elapsedRealtime();
            return ((MyApplication)getApplication()).speak(msg);
        }
    }

    WaitDialog waitDialog;

    public void showWaitDialog() {
        if (waitDialog == null) {
            waitDialog = new WaitDialog(this);
        }
        if (!waitDialog.isShowing()) {
            waitDialog.show();
        }
    }

    public void hideWaitDialog() {
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }
}
