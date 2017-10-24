package com.bestxty.sault.demo

import android.content.Context

/**
 * @author 姜泰阳
 *         Created by 姜泰阳 on 2017/10/19.
 */

interface SingleTaskView {

    fun getContext(): Context

    fun showProgress(totalSize: Long, finishedSize: Long)

    fun clearProgress()

    fun showError(msg: String?)

    fun showStatus(status: String?)

    fun enableStartBtn()

    fun enablePauseAndCancelBtn()

    fun enableResumeAndCancelBtn()
}
