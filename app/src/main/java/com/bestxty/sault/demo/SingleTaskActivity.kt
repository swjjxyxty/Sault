package com.bestxty.sault.demo

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.bestxty.sault.Sault
import kotlinx.android.synthetic.main.activity_single_task.*

class SingleTaskActivity : AppCompatActivity(), View.OnClickListener, SingleTaskView {


    private lateinit var singleViewController: SingleViewController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_task)
        btn_start.setOnClickListener(this)
        btn_pause.setOnClickListener(this)
        btn_resume.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)

        singleViewController = SingleViewController()
        singleViewController.attachView(this)

        enableStartBtn()
        showStatus("Ready")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> singleViewController.start("http://download.ydstatic.cn/cidian/static/7.0/20170222/YoudaoDictSetup.exe")
            R.id.btn_pause -> singleViewController.pause()
            R.id.btn_resume -> singleViewController.resume()
            R.id.btn_cancel -> singleViewController.cancel()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    override fun showError(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun clearProgress() {
        pb_progress.progress = 0
        text_progress_tip.text = ""
    }


    override fun showProgress(totalSize: Long, finishedSize: Long) {
        pb_progress.progress = Sault.calculateProgress(finishedSize, totalSize)
        text_progress_tip.text = String.format(getString(R.string.progress_tip), finishedSize, totalSize)
    }

    override fun showStatus(status: String?) {
        text_status.text = status
    }

    override fun getContext(): Context = this.applicationContext


    override fun enableResumeAndCancelBtn() {
        btn_start.isEnabled = false
        btn_pause.isEnabled = false
        btn_resume.isEnabled = true
        btn_cancel.isEnabled = true
    }

    override fun enablePauseAndCancelBtn() {
        btn_start.isEnabled = false
        btn_pause.isEnabled = true
        btn_resume.isEnabled = false
        btn_cancel.isEnabled = true
    }


    override fun enableStartBtn() {
        btn_start.isEnabled = true
        btn_pause.isEnabled = false
        btn_resume.isEnabled = false
        btn_cancel.isEnabled = false
    }
}
