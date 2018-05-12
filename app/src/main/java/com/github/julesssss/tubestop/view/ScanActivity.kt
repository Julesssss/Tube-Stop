package com.github.julesssss.tubestop.view

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
import com.github.julesssss.tubestop.R
import com.github.julesssss.tubestop.data.AppDatabase
import com.github.julesssss.tubestop.data.WifiPoint
import com.github.julesssss.tubestop.data.WifiPointDao
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.toast
import com.github.julesssss.tubestop.util.CsvReader
import com.github.julesssss.tubestop.util.sqlAsset.AssetSQLiteOpenHelperFactory
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

//                val scanResults = wifiManager.scanResults
                val scanResult = listOf("a4:56:30:cc:7a:40", "c8:f9:f9:a7:87:70", "2c:36:f8:b9:56:e0",
                        "c8:f9:f9:5b:9e:90", "3c:ce:73:70:64:20", "3c:ce:73:f6:d2:c0", "3c:ce:73:70:6b:e0",
                        "c8:f9:f9:72:3b:c0", "3c:ce:73:f6:a2:70", "c8:f9:f9:28:ea:40", "c8:f9:f9:5b:ac:30")

                detectCurrentStation(scanResult)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Use prebuilt database, refactor to class
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wifi_points_from_device.db")
                .openHelperFactory(AssetSQLiteOpenHelperFactory())
                .allowMainThreadQueries() // todo: make asynchronous wth RxJava
                .build()
        dao = db.wifiPointDao()

        // WiFi scanning setup
        wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        registerReceiver(wifiScanReceiver, IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION))

        // basic Button For searching
        fab.setOnClickListener {
            attemptScan()

//            val wifiPoints = CsvReader().getWifiPointsFromCSV(this.applicationContext)
//            dao.insertWifiPoints(wifiPoints)
        }

        setupStationArray()
    }

    private fun saveResultsToDatabase(scanResults: List<ScanResult>) {
        val wifiPoints = mutableListOf<WifiPoint>()

        scanResults.map {
            val wifiPoint = WifiPoint()
            wifiPoint.bssid = it.BSSID
            wifiPoint.station = selectedStation
            wifiPoints.add(wifiPoint)
        }

        dao.insertWifiPoints(wifiPoints)
        displayDatabaseInformation()
        toast("${wifiPoints.size} found, networks ssid's -> ${scanResults.map { ", ${it.SSID}" }}")
    }

    private fun detectCurrentStation(scanResults: List<String>) {
        val matchingPoints = dao.findWifiPoints(scanResults)



        toast("${matchingPoints.map { it.station }}")
    }

    private fun displayDatabaseInformation() {
        val totalPoints = dao.findAllWifiPoints().size
        val allForStation = dao.findWifiPointsForStation(selectedStation)

        textResults.text = "${allForStation.size} for current station\n$totalPoints in database"
    }

    private fun setupStationArray() {
        stationAdapter = ArrayAdapter.createFromResource(applicationContext, R.array.victoria_line, android.R.layout.simple_spinner_dropdown_item)
        spinnerStations.adapter = stationAdapter
        spinnerStations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onStationSelected(position)
            }
        }
    }

    private fun onStationSelected(position: Int) {
        val stationArray = resources.getStringArray(R.array.victoria_line)
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