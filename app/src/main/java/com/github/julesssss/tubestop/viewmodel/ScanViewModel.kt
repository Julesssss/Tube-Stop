package com.github.julesssss.tubestop.viewmodel

import android.app.Application
import android.net.wifi.ScanResult
import com.github.julesssss.tubestop.wifiscanner.WifiScanReceiver

class ScanViewModel(
        context: Application,
        callback: ((results: List<ScanResult>) -> Unit),
        private val wifiScanReceiver: WifiScanReceiver = WifiScanReceiver(context, callback)
) {

    fun getWifiScanResults() {
        wifiScanReceiver.startScan()
    }

    fun unRegisterReciever() {
        wifiScanReceiver.deregisterDevice()
    }

}