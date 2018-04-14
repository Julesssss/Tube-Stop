package com.github.julesssss.tubestop

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
import com.github.julesssss.tubestop.data.WifiPoint
import com.github.julesssss.tubestop.data.WifiPointDao
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.toast
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : Activity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var db: AppDatabase
    private lateinit var dao: WifiPointDao

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val scanResults = wifiManager.scanResults

                val wifiPoints = mutableListOf<WifiPoint>()
                scanResults.map {
                    val wifiPoint = WifiPoint()
                    wifiPoint.bssid = it.BSSID
                    wifiPoint.ssid = it.SSID
                    wifiPoint.timestamp = it.timestamp
                    wifiPoints.add(wifiPoint)
                }

                dao.insertWifiPoints(wifiPoints)

                toast("Found wifi networks -> ${scanResults.map {", ${it.SSID}" }}")
                scanResults.map {
                    log(it.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wifi_points")
                .allowMainThreadQueries()
                .build()
        dao = db.wifiPointDao()

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

        val points = dao.findAllWifiPoints()

        toast("${points.size} saved points ")

    }


}