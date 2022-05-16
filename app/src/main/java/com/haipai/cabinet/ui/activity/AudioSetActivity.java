package com.haipai.cabinet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

import com.haipai.cabinet.R;
import com.haipai.cabinet.util.CustomMethodUtil;
import com.haipai.cabinet.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class AudioSetActivity extends BaseActivity {
    @OnClick(R.id.btn_back)
    public void onActionBack() {
        finish();
    }

    @BindView(R.id.seekbar_audio)
    SeekBar seekBarAudio;

    @OnClick(R.id.btn_test)
    public void onActionTset() {
        speak("The volume test");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_set);
    }
    @Override
    public void initUIView() {
        seekBarAudio.setProgress(CustomMethodUtil.getAudioSet());

        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {

                CustomMethodUtil.setAudioSet(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}