package com.github.julesssss.tubestop

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private lateinit var stationAdapter: ArrayAdapter<CharSequence>
    private lateinit var selectedStation: String

    private val wifiScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {

                if (switchDetectCurrentStation.isChecked) {
                    detectCurrentStation(wifiManager.scanResults)
                } else {
                    saveResultsToDatabase(wifiManager.scanResults)
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

        setupStationArray()
    }

    private fun saveResultsToDatabase(scanResults: List<ScanResult>) {
        val wifiPoints = mutableListOf<WifiPoint>()

        scanResults.map {
            val wifiPoint = WifiPoint()
            wifiPoint.bssid = it.BSSID
            wifiPoint.ssid = it.SSID
            wifiPoint.timestamp = it.timestamp
            wifiPoint.station = selectedStation
            wifiPoints.add(wifiPoint)
        }

        dao.insertWifiPoints(wifiPoints)
        displayDatabaseInformation()
        toast("${wifiPoints.size} found, networks ssid's -> ${scanResults.map { ", ${it.SSID}" }}")
    }

    private fun detectCurrentStation(scanResults: MutableList<ScanResult>) {
        val matchingPoints = dao.findWifiPoints(scanResults.map { it.BSSID })
        toast("${matchingPoints.map { it.station }}")
    }

    private fun displayDatabaseInformation() {
        val totalPoints = dao.findAllWifiPoints().size
        val allForStation = dao.findWifiPointsForStation(selectedStation)

        textResults.text = "${allForStation.size} for current station\n$totalPoints in database"
    }

    private fun setupStationArray() {
        stationAdapter = ArrayAdapter.createFromResource(applicationContext, R.array.northern_line, android.R.layout.simple_spinner_dropdown_item)
        spinnerStations.adapter = stationAdapter
        spinnerStations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onStationSelected(position)
            }
        }
    }

    private fun onStationSelected(position: Int) {
        val stationArray = resources.getStringArray(R.array.northern_line)
        selectedStation = stationArray[position]
        displayDatabaseInformation()
        toast("Selected: $selectedStation")
    }

    private fun attemptScan() {
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