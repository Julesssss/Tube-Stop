package com.github.julesssss.tubestop.wifiscanner

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager

class WifiScanReceiver(
        private val context: Application,
        private var callback: ((results: List<ScanResult>) -> Unit)?,
        private val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
) : BroadcastReceiver() {

    init {
        context.registerReceiver(this, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {

            callback?.invoke(wifiManager.scanResults)

        }
    }

    fun startScan() {
        wifiManager.startScan()
    }

    fun deregisterDevice() {
        callback = null
        context.unregisterReceiver(this)
    }

}