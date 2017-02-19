package com.bestxty.sault.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

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

import static com.bestxty.sault.demo.adapter.TaskAdapter.TaskViewHolder;

public class MutilTaskActivity extends AppCompatActivity {


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
                .build();

        initTaskList();
        adapter = new TaskAdapter(this, null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                TaskViewHolder holder = (TaskViewHolder) view.getTag();
                handleClick(index, holder);
            }
        });

    }

    private void handleClick(int index, TaskViewHolder holder) {
        Task task = taskList.get(index);
        Object tag = sault.load(task.getUrl())
                .tag(task.getUrl())
                .priority(Sault.Priority.NORMAL)
                .listener(new TaskCallback(holder.button, holder.progressBar))
                .go();
        task.setTag(tag);
    }

    private void initTaskList() {
        taskList = new ArrayList<>();
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

    private static class TaskCallback implements Callback {

        private Button button;
        private ProgressBar progressBar;

        public TaskCallback(Button button, ProgressBar progressBar) {
            this.button = button;
            this.progressBar = progressBar;
        }

        @Override
        public void onEvent(Object tag, int event) {

        }

        @Override
        public void onProgress(Object tag, long totalSize, long finishedSize) {

        }

        @Override
        public void onComplete(Object tag, String path) {

        }

        @Override
        public void onError(SaultException exception) {

        }
    }
}
