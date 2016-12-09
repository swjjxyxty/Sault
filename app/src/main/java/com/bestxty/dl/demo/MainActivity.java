package com.bestxty.dl.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mTextView;
    private TextView mSpeed;
    private TextView mTextView2;
    private TextView mSpeed2;
    private TextView mTextView3;
    private TextView mSpeed3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text_progress);
        mSpeed = (TextView) findViewById(R.id.text_speed);
        mTextView2 = (TextView) findViewById(R.id.text_progress2);
        mSpeed2 = (TextView) findViewById(R.id.text_speed2);
        mTextView3 = (TextView) findViewById(R.id.text_progress3);
        mSpeed3 = (TextView) findViewById(R.id.text_speed3);
        Log.d(TAG, "rest--------------------------------");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadApplication.getRefWatcher(this)
                .watch(this);

    }
}
