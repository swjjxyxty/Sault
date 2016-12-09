package com.bestxty.dl.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bestxty.dl.Sault;

import java.io.File;

public class SingleTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SingleTaskActivity";

    private Button startBtn;
    private Button puaseBtn;
    private Button resumeBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;

    private Object tag;

    private Sault sault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        startBtn = (Button) findViewById(R.id.btn_start);
        puaseBtn = (Button) findViewById(R.id.btn_pause);
        resumeBtn = (Button) findViewById(R.id.btn_resume);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);

        startBtn.setOnClickListener(this);
        puaseBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);


        enableStartBtn();

        sault = new Sault.Builder(this)
                .saveDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dl_test")
                .build();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                tag = sault.load("http://192.168.99.200:8000/test_5.pdf")
                        .tag("test-task")
                        .priority(Sault.Priority.HIGH)
                        .go();
                if (tag != null) {
                    enablePauseAndCancelBtn();
                }
                break;
            case R.id.btn_pause:
                sault.pause(tag);
                break;
            case R.id.btn_resume:
                sault.resume(tag);
                break;
            case R.id.btn_cancel:
                sault.cancel(tag);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sault.shutdown();
    }


    private void enableResumeBtn() {
        startBtn.setEnabled(false);
        puaseBtn.setEnabled(false);
        resumeBtn.setEnabled(true);
        cancelBtn.setEnabled(false);
    }

    private void enablePauseAndCancelBtn() {
        startBtn.setEnabled(false);
        puaseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
    }

    private void enableStartBtn() {
        startBtn.setEnabled(true);
        puaseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }
}
