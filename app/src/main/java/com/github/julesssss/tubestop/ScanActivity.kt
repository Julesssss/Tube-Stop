package com.github.julesssss.tubestop

import android.app.Activity
import android.os.Bundle
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.toast
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // basic Button For searching
        fab.setOnClickListener {
            attemptScan()
        }
    }

    private fun attemptScan() {
        // test extensions
        toast("FAB clicked")
        log("FAB clicked")
    }
}