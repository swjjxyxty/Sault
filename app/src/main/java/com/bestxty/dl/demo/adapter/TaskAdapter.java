package com.bestxty.dl.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestxty.dl.demo.R;
import com.bestxty.dl.demo.bean.Task;

import java.util.List;

/**
 * @author swjjx
 *         Created by swjjx on 2016/12/20.
 */

public class TaskAdapter extends BaseAdapter {

    private List<Task> taskList;
    private LayoutInflater layoutInflater;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int i) {
        return taskList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TaskViewHolder holder = null;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_task, viewGroup, false);
            holder = new TaskViewHolder();
            holder.title = (TextView) view.findViewById(R.id.text_title);
            holder.desc = (TextView) view.findViewById(R.id.text_desc);
            holder.button = (Button) view.findViewById(R.id.btn_task);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.pb_progress);
            view.setTag(holder);
        } else {
            holder = (TaskViewHolder) view.getTag();
        }

        Task task = taskList.get(i);
        holder.title.setText(task.getTitle());
        holder.desc.setText(task.getDesc());
        holder.button.setText(getTaskState(task.getState()));
        if (task.getState() == Task.STATE_READY) {
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private String getTaskState(int state) {
        switch (state) {
            case Task.STATE_READY:
                return "Start";
            case Task.STATE_DOWNING:
                return "DOWN";
            case Task.STATE_PAUSE:
                return "PAUSE";
            case Task.STATE_DONE:
                return "DONE";
        }
        return "ERROR";
    }

    public static class TaskViewHolder {
        TextView title;
        TextView desc;
       public Button button;
       public ProgressBar progressBar;
    }
}
