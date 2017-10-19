package com.bestxty.sault.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestxty.sault.Sault;

public class SingleTaskActivity extends AppCompatActivity implements View.OnClickListener, SingleTaskView {

    private Button startBtn;
    private Button pauseBtn;
    private Button resumeBtn;
    private Button cancelBtn;
    private ProgressBar progressBar;
    private TextView status;
    private TextView progressTip;

    private SingleViewController singleViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_task);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        startBtn = (Button) findViewById(R.id.btn_start);
        pauseBtn = (Button) findViewById(R.id.btn_pause);
        resumeBtn = (Button) findViewById(R.id.btn_resume);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        status = (TextView) findViewById(R.id.text_status);
        progressTip = (TextView) findViewById(R.id.text_progress_tip);

        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        singleViewController = new SingleViewController();
        singleViewController.attachView(this);

        enableStartBtn();
        showStatus("Ready");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                //http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe
//                tag = sault.load("http://192.168.99.200:8000/WeChat_C1018.exe")
                singleViewController.start("http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe");
                break;
            case R.id.btn_pause:
                singleViewController.pause();
                break;
            case R.id.btn_resume:
                singleViewController.resume();
                break;
            case R.id.btn_cancel:
                singleViewController.cancel();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearProgress() {
        progressBar.setProgress(0);
        progressTip.setText("");
    }

    @Override
    public void showProgress(long totalSize, long finishedSize) {
        progressBar.setProgress(Sault.calculateProgress(finishedSize, totalSize));
        progressTip.setText(String.format(getString(R.string.progress_tip), finishedSize, totalSize));
    }

    @Override
    public void showStatus(String status) {
        this.status.setText(status);
    }

    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public void enableResumeAndCancelBtn() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    @Override
    public void enablePauseAndCancelBtn() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
    }


    @Override
    public void enableStartBtn() {
        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }
}
