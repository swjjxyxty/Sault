package com.bestxty.sault.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bestxty.sault.Sault
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_single.setOnClickListener { startActivity(Intent(this@MainActivity, SingleTaskActivity::class.java)) }
        text_multi.setOnClickListener { startActivity(Intent(this@MainActivity, MutilTaskActivity::class.java)) }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Sault.getInstance(this).close()
        DownloadApplication.getRefWatcher(this)
                .watch(this)

    }
}
