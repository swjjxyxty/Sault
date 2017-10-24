package com.bestxty.sault.demo

import android.app.Application
import android.content.Context
import android.os.Environment
import com.bestxty.sault.Sault
import com.bestxty.sault.SaultConfiguration
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File

/**
 * @author xty
 *         Created by xty on 2016/11/29.
 */
class DownloadApplication : Application() {

    lateinit var mRefWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        mRefWatcher = LeakCanary.install(this)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val saultConfiguration: SaultConfiguration = SaultConfiguration.Builder()
                .saveDir(Environment.getExternalStorageDirectory()
                        .absolutePath + File.separator + "sault_test")
                .client(OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build())
                .build()
        Sault.setDefaultConfiguration(saultConfiguration)

    }


    companion object {
        fun getRefWatcher(context: Context): RefWatcher {
            val downloadApplication = context.applicationContext as DownloadApplication
            return downloadApplication.mRefWatcher
        }
    }

}
