package com.bestxty.sault.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.bestxty.sault.demo.R
import com.bestxty.sault.demo.bean.Task

/**
 * @author swjjx
 *         Created by swjjx on 2016/12/20.
 */

class TaskAdapter(context: Context, private var taskList: List<Task>) : BaseAdapter() {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Any = taskList[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: TaskViewHolder
        val view: View
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_task, parent, false)
            holder = TaskViewHolder()
            holder.title = view.findViewById(R.id.text_title) as TextView
            holder.desc = view.findViewById(R.id.text_desc) as TextView
            holder.button = view.findViewById(R.id.btn_task) as Button
            holder.progressBar = view.findViewById(R.id.pb_progress) as ProgressBar
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as TaskViewHolder
        }
        val task: Task = taskList[position]
        holder.title.text = task.title
        holder.desc.text = task.desc
        holder.button.text = getTaskState(task.state)
        if (task.state == Task.STATE_READY
                || task.state == Task.STATE_DONE) {
            holder.progressBar.visibility = View.GONE
        } else {
            holder.progressBar.visibility = View.VISIBLE
        }
        return view
    }


    private fun getTaskState(state: Int): String {
        when (state) {
            Task.STATE_READY ->
                return "Start"
            Task.STATE_DOWNING ->
                return "DOWN"
            Task.STATE_PAUSE ->
                return "PAUSE"
            Task.STATE_DONE ->
                return "DONE"
        }
        return "ERROR"
    }

    class TaskViewHolder {
        lateinit var title: TextView
        lateinit var desc: TextView
        lateinit var button: Button
        lateinit var progressBar: ProgressBar
    }

}
