package com.github.julesssss.tubestop

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.toast
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : Activity() {

    private lateinit var wifiManager: WifiManager

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val scanResults = wifiManager.scanResults
                toast("Found wifi networks -> ${scanResults.map {", ${it.SSID}" }}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // WiFi scanning setup
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        registerReceiver(wifiScanReceiver, IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION))

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