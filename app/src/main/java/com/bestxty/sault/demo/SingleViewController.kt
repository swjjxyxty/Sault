package com.bestxty.sault.demo

import android.util.Log

import com.bestxty.sault.Callback
import com.bestxty.sault.Callback.*
import com.bestxty.sault.Sault
import com.bestxty.sault.SaultException

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */

class SingleViewController : Callback {

    private lateinit var singleTaskView: SingleTaskView

    private lateinit var sault: Sault

    private var tag: Any? = null

    private val finish: AtomicBoolean = AtomicBoolean(true)

    fun attachView(singleTaskView: SingleTaskView) {
        this.singleTaskView = singleTaskView
        sault = Sault.getInstance(singleTaskView.getContext())
    }


    fun start(url: String) {
        if (tag != null && !finish.get()) {
            return
        }
        tag = sault.load(url)
                .tag("test-task")
                .listener(this)
                .breakPointEnabled(true)
                .multiThreadEnabled(true)
                .priority(Sault.Priority.HIGH)
                .go()
    }


    fun pause() {
        if (tag == null) return
        sault.pause(tag)
    }

    fun cancel() {
        if (tag == null) return
        sault.cancel(tag)
    }

    fun resume() {
        if (tag == null) return
        sault.resume(tag)
    }

    override fun onError(exception: SaultException) {
        singleTaskView.showError(exception.message)
        singleTaskView.enableStartBtn()
        exception.printStackTrace()
    }

    override fun onEvent(tag: Any, event: Int) {
        when (event) {
            EVENT_START, EVENT_RESUME ->
                singleTaskView.enablePauseAndCancelBtn()
            EVENT_PAUSE ->
                singleTaskView.enableResumeAndCancelBtn()
            EVENT_CANCEL ->
                singleTaskView.enableStartBtn()
        }
        when (event) {
            EVENT_START -> {
                finish.set(false)
                singleTaskView.showStatus("Running")
            }
            EVENT_PAUSE ->
                singleTaskView.showStatus("Paused")

            EVENT_RESUME ->
                singleTaskView.showStatus("Running")

            EVENT_CANCEL -> {
                singleTaskView.showStatus("Canceled")
                finish.set(true)
                singleTaskView.clearProgress()
            }
            EVENT_COMPLETE ->
                singleTaskView.showStatus("Complete")

        }

    }

    override fun onProgress(tag: Any?, totalSize: Long, finishedSize: Long) {
        singleTaskView.showProgress(totalSize, finishedSize)
    }

    override fun onComplete(tag: Any?, path: String?) {
        this.tag = null
        finish.set(true)
        singleTaskView.enableStartBtn()
        Log.d("SingleViewController", path)
    }
}
