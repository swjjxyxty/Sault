package com.bestxty.sault.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestxty.sault.Callback;
import com.bestxty.sault.Sault;
import com.bestxty.sault.SaultException;
import com.bestxty.sault.demo.adapter.TaskAdapter;
import com.bestxty.sault.demo.bean.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.bestxty.sault.Sault.calculateProgress;
import static com.bestxty.sault.demo.adapter.TaskAdapter.TaskViewHolder;

public class MutilTaskActivity extends AppCompatActivity {

    private static final String TAG = "MutilTaskActivity";

    private ListView listView;

    private TaskAdapter adapter;
    private List<Task> taskList;

    private Sault sault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_task);
        listView = (ListView) findViewById(R.id.listview);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        sault = new Sault.Builder(this)
                .saveDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dl_test")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build())
                .loggingEnabled(true)
                .build();

        initTaskList();
        adapter = new TaskAdapter(this, taskList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                Log.d(TAG, "onItemClick() called with: index = [" + index + "], id = [" + id + "]");
                TaskViewHolder holder = (TaskViewHolder) view.getTag();
                handleClick(index, holder);
            }
        });

    }

    private void handleClick(int index, TaskViewHolder holder) {
        Log.d(TAG, "handleClick() called with: index = [" + index + "], holder = [" + holder + "]");
        Task task = taskList.get(index);

        if (task.getState() == Task.STATE_READY
                || task.getState() == Task.STATE_DONE) {
            Object tag = sault.load(task.getUrl())
                    .tag(task.getUrl())
                    .priority(task.getPriority())
                    .listener(new TaskCallback(task, holder.button, holder.progressBar, holder.desc))
                    .go();
            task.setTag(tag);
        } else if (task.getState() == Task.STATE_DOWNING) {
            sault.pause(task.getTag());
        } else if (task.getState() == Task.STATE_PAUSE) {
            sault.resume(task.getTag());
        }
    }

    private void initTaskList() {
        taskList = new ArrayList<>();
        String baseUrl = "http://192.168.99.200:8000/";
        taskList.add(new Task("test-task-0", "desc--0", baseUrl + "file0.exe", Sault.Priority.LOW));
        taskList.add(new Task("test-task-1", "desc--1", baseUrl + "file1.exe", Sault.Priority.LOW));
        taskList.add(new Task("test-task-2", "desc--2", baseUrl + "file2.exe", Sault.Priority.LOW));

        taskList.add(new Task("test-task-3", "desc--3", baseUrl + "file3.exe", Sault.Priority.NORMAL));
        taskList.add(new Task("test-task-4", "desc--4", baseUrl + "file4.exe", Sault.Priority.NORMAL));
        taskList.add(new Task("test-task-5", "desc--5", baseUrl + "file5.exe", Sault.Priority.NORMAL));

        taskList.add(new Task("test-task-6", "desc--6", baseUrl + "file6.exe", Sault.Priority.HIGH));
        taskList.add(new Task("test-task-7", "desc--7", baseUrl + "file7.exe", Sault.Priority.HIGH));
        taskList.add(new Task("test-task-8", "desc--8", baseUrl + "file8.exe", Sault.Priority.HIGH));

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
        DownloadApplication.getRefWatcher(this)
                .watch(this);
        DownloadApplication.getRefWatcher(this)
                .watch(sault);
    }

    private static class TaskCallback implements Callback {

        private Button button;
        private ProgressBar progressBar;
        private TextView desc;
        private Task task;

        public TaskCallback(Task task, Button button, ProgressBar progressBar, TextView desc) {
            this.task = task;
            this.button = button;
            this.progressBar = progressBar;
            this.desc = desc;
        }

        private void hideProgressBar() {
            desc.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        private void showProgressBar() {
            desc.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        private void showPauseButton() {
            button.setText("Pause");
        }

        private void showResumeButton() {
            button.setText("Resume");
        }

        private void showStartButton() {
            button.setText("Start");
        }

        @Override
        public void onEvent(Object tag, int event) {
            Log.d(TAG, "onEvent() called with: tag = [" + tag + "], event = [" + event + "]");
            if (event == EVENT_PAUSE) {
                showResumeButton();
                task.setState(Task.STATE_PAUSE);
            } else if (event == EVENT_START
                    || event == EVENT_RESUME) {
                showProgressBar();
                showPauseButton();
                task.setState(Task.STATE_DOWNING);
            } else if (event == EVENT_COMPLETE
                    || event == EVENT_CANCEL) {
                hideProgressBar();
                showStartButton();
                task.setState(Task.STATE_DONE);
            }

        }

        @Override
        public void onProgress(Object tag, long totalSize, long finishedSize) {
            progressBar.setProgress(calculateProgress(finishedSize, totalSize));
        }

        @Override
        public void onComplete(Object tag, String path) {
            Log.d(TAG, "onComplete() called with: tag = [" + tag + "], path = [" + path + "]");
        }

        @Override
        public void onError(SaultException exception) {
            Log.e(TAG, "onError: " + exception.getMessage(), exception);
            task.setState(Task.STATE_DONE);
        }
    }
}
