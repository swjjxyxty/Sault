package com.bestxty.sault.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bestxty.sault.Callback;
import com.bestxty.sault.Sault;
import com.bestxty.sault.SaultException;

public class SingleTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SingleTaskActivity";

    private Button startBtn;
    private Button pauseBtn;
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
        pauseBtn = (Button) findViewById(R.id.btn_pause);
        resumeBtn = (Button) findViewById(R.id.btn_resume);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);

        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resumeBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);


        enableStartBtn();


        sault = Sault.getInstance(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                //http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe
//                tag = sault.load("http://192.168.99.200:8000/WeChat_C1018.exe")
                tag = sault.load("http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe")
                        .tag("test-task")
                        .listener(new Callback() {
                            @Override
                            public void onEvent(Object tag, int event) {
                                Log.d(TAG, "onEvent() called with: tag = [" + tag + "], event = [" + event + "]");
                                if (event == EVENT_PAUSE) {
                                    enableResumeBtn();
                                } else if (event == EVENT_START
                                        || event == EVENT_RESUME) {
                                    enablePauseAndCancelBtn();
                                } else if (event == EVENT_COMPLETE
                                        || event == EVENT_CANCEL) {
                                    enableStartBtn();
                                }

                                System.out.println(sault.getStats());
                            }

                            @Override
                            public void onProgress(Object tag, long totalSize, long finishedSize) {
                                try {
                                    int progress = (int) (finishedSize * 100 / totalSize);
                                    progressBar.setProgress(progress);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onComplete(Object tag, String path) {
                                Log.d(TAG, "onComplete() called with: tag = [" + tag + "], path = [" + path + "]");
                            }

                            @Override
                            public void onError(SaultException exception) {
                                Log.d(TAG, "onError() called with: exception = [" + exception + "]");
                                enableStartBtn();
                                System.out.println(sault.getStats());
                            }
                        })
                        .priority(Sault.Priority.HIGH)
                        .multiThreadEnabled(true)
                        .breakPointEnabled(true)
                        .go();
                if (tag != null) {
                    enablePauseAndCancelBtn();
                }
                break;
            case R.id.btn_pause:
                sault.pause(tag);
//                System.out.println(sault.getStats());
                break;
            case R.id.btn_resume:
                sault.resume(tag);
//                System.out.println(sault.getStats());
                break;
            case R.id.btn_cancel:
                sault.cancel(tag);
//                System.out.println(sault.getStats());
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
        sault.close();
    }


    private void enableResumeBtn() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    private void enablePauseAndCancelBtn() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
    }

    private void enableStartBtn() {
        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }
}
