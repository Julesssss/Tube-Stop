package com.github.julesssss.tubestop.view

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
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
import com.github.julesssss.tubestop.data.AppDatabase
import com.github.julesssss.tubestop.data.WifiPointDao
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.toast
import com.github.julesssss.tubestop.util.sqlAsset.AssetSQLiteOpenHelperFactory

abstract class ScanActivity : Activity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var db: AppDatabase
    private lateinit var dao: WifiPointDao

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {

//                val scanResults = wifiManager.scanResults
                val scanResult = listOf("a4:56:30:cc:7a:40", "c8:f9:f9:a7:87:70", "2c:36:f8:b9:56:e0",
                        "c8:f9:f9:5b:9e:90", "3c:ce:73:70:64:20", "3c:ce:73:f6:d2:c0", "3c:ce:73:70:6b:e0",
                        "c8:f9:f9:72:3b:c0", "3c:ce:73:f6:a2:70", "c8:f9:f9:28:ea:40", "c8:f9:f9:5b:ac:30")

                detectCurrentStation(scanResult)
            }
        }
    }

    abstract fun onStationFound(scanResults: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use prebuilt database, refactor to class
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wifi_points_from_device.db")
                .openHelperFactory(AssetSQLiteOpenHelperFactory())
                .allowMainThreadQueries() // todo: make asynchronous wth RxJava
                .build()
        dao = db.wifiPointDao()

        // WiFi scanning setup
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        registerReceiver(wifiScanReceiver, IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION))
    }

    // Refactor to VM
    private fun detectCurrentStation(scanResults: List<String>) {
        val matchingPoints = dao.findWifiPoints(scanResults)

        matchingPoints.firstOrNull()?.let {
            onStationFound(it.station)
        }

        toast("${matchingPoints.map { it.station }}")
    }

    protected fun attemptScan() {
        log("attemptScan")

        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            wifiManager.startScan()
        }
    }


}