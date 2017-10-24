package com.bestxty.sault.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.bestxty.sault.Callback
import com.bestxty.sault.Callback.*
import com.bestxty.sault.Sault
import com.bestxty.sault.Sault.calculateProgress
import com.bestxty.sault.SaultException
import com.bestxty.sault.demo.adapter.TaskAdapter
import com.bestxty.sault.demo.bean.Task
import kotlinx.android.synthetic.main.activity_mutil_task.*


class MutilTaskActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MutilTaskActivity"
    }

    private lateinit var listView: ListView
    private lateinit var adapter: TaskAdapter
    private lateinit var taskList: List<Task>
    private lateinit var sault: Sault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mutil_task)
        listView = listview
        sault = Sault.getInstance(this)
        initTaskList()
        adapter = TaskAdapter(this, taskList)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, view, position, _ ->
            run {
                val holder: TaskAdapter.TaskViewHolder = view.tag as TaskAdapter.TaskViewHolder
                handleClick(position, holder)
            }
        }
    }

    private fun handleClick(index: Int, holder: TaskAdapter.TaskViewHolder) {
        Log.d(TAG, "handleClick() called with: index = [$index], holder = [$holder]")
        val task = taskList[index]

        if (task.state == Task.STATE_READY
                || task.state == Task.STATE_DONE) {
            val tag = sault.load(task.url)
                    .tag(task.url)
                    .priority(task.priority)
                    .listener(TaskCallback(task, holder.button, holder.progressBar, holder.desc))
                    .go()
            task.tag = tag
        } else if (task.state == Task.STATE_DOWNING) {
            sault.pause(task.tag)
        } else if (task.state == Task.STATE_PAUSE) {
            sault.resume(task.tag)
        }
    }

    private fun initTaskList() {

        val baseUrl = "http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe"
        val taskList = ArrayList<Task>()
        taskList.add(Task("test-task-0", "desc--0", baseUrl, Sault.Priority.LOW))
        taskList.add(Task("test-task-1", "desc--1", baseUrl, Sault.Priority.LOW))
        taskList.add(Task("test-task-2", "desc--2", baseUrl, Sault.Priority.LOW))

        taskList.add(Task("test-task-3", "desc--3", baseUrl, Sault.Priority.NORMAL))
        taskList.add(Task("test-task-4", "desc--4", baseUrl, Sault.Priority.NORMAL))
        taskList.add(Task("test-task-5", "desc--5", baseUrl, Sault.Priority.NORMAL))

        taskList.add(Task("test-task-6", "desc--6", baseUrl, Sault.Priority.HIGH))
        taskList.add(Task("test-task-7", "desc--7", baseUrl, Sault.Priority.HIGH))
        taskList.add(Task("test-task-8", "desc--8", baseUrl, Sault.Priority.HIGH))

        this.taskList = taskList
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        sault.close()
        DownloadApplication.Companion.getRefWatcher(this)
                .watch(this)
        DownloadApplication.Companion.getRefWatcher(this)
                .watch(sault)
    }


    private class TaskCallback(var task: Task, var button: Button, var progressBar: ProgressBar,
                               var desc: TextView) : Callback {

        fun hideProgressBar() {
            desc.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        fun showProgressBar() {
            desc.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }

        fun showPauseButton() {
            button.text = "Pause"
        }

        fun showResumeButton() {
            button.text = "Resume"
        }

        fun showStartButton() {
            button.text = "Start"
        }

        override fun onEvent(tag: Any, event: Int) {
            Log.d(TAG, "onEvent() called with: tag = [$tag], event = [$event]")
            if (event == EVENT_PAUSE) {
                showResumeButton()
                task.state = Task.STATE_PAUSE
            } else if (event == EVENT_START
                    || event == EVENT_RESUME) {
                showProgressBar()
                showPauseButton()
                task.state = Task.STATE_DOWNING
            } else if (event == EVENT_COMPLETE
                    || event == EVENT_CANCEL) {
                hideProgressBar()
                showStartButton()
                task.state = Task.STATE_DONE
            }

        }

        override fun onProgress(tag: Any?, totalSize: Long, finishedSize: Long) {
            progressBar.progress = calculateProgress(finishedSize, totalSize)
        }

        override fun onComplete(tag: Any?, path: String?) {
            Log.d(TAG, "onComplete() called with: tag = [$tag], path = [$path]")
        }

        override fun onError(exception: SaultException?) {
            Log.e(TAG, "onError: " + exception?.message, exception)
            task.state = (Task.STATE_DONE)
        }
    }
}
