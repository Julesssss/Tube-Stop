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
        log("attemptScan")

        // check permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) ,0)
        } else {
            wifiManager.startScan()
        }

    }


}