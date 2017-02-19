package com.bestxty.sault.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bestxty.sault.Callback;
import com.bestxty.sault.Sault;
import com.bestxty.sault.SaultException;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

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

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        sault = new Sault.Builder(this)
                .saveDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dl_test")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build())
                .build();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                tag = sault.load("http://192.168.99.200:8000/WeChat_C1018.exe")
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
                                int progress = (int) (finishedSize * 100 / totalSize);
                                progressBar.setProgress(progress);
                            }

                            @Override
                            public void onComplete(Object tag, String path) {
                                Log.d(TAG, "onComplete() called with: tag = [" + tag + "], path = [" + path + "]");
                            }

                            @Override
                            public void onError(SaultException exception) {
                                Log.d(TAG, "onError() called with: exception = [" + exception + "]");
                            }
                        })
                        .priority(Sault.Priority.HIGH)
                        .multiThreadEnabled(false)
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
        sault.shutdown();
    }


    private void enableResumeBtn() {
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(false);
        resumeBtn.setEnabled(true);
        cancelBtn.setEnabled(false);
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
