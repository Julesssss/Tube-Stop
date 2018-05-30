package com.github.julesssss.tubestop.view

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.julesssss.tubestop.data.AppDatabase
import com.github.julesssss.tubestop.data.WifiPointDao
import com.github.julesssss.tubestop.extensions.log
import com.github.julesssss.tubestop.extensions.longToast
import com.github.julesssss.tubestop.extensions.toast
import com.github.julesssss.tubestop.util.sqlAsset.AssetSQLiteOpenHelperFactory
import com.github.julesssss.tubestop.viewmodel.ScanViewModel
import kotlinx.android.synthetic.main.activity_main.*

abstract class ScanActivity : Activity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: WifiPointDao

    private lateinit var scanViewModel: ScanViewModel

    abstract fun onStationFound(station: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scanViewModel = ScanViewModel(application, {
            detectCurrentStation(it.map { it.BSSID })
        })

        // Use prebuilt database todo: refactor to class
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "wifi_points_from_device.db")
                .openHelperFactory(AssetSQLiteOpenHelperFactory())
                .allowMainThreadQueries() // todo: make asynchronous wth RxJava
                .build()
        dao = db.wifiPointDao()

        // Start listening for results
        scanViewModel.getWifiScanResults()
    }

    override fun onDestroy() {
        super.onDestroy()

        scanViewModel.unRegisterReciever() // todo: make VM lifecycle aware
    }

    // Refactor to VM
    private fun detectCurrentStation(scanResults: List<String>) {
        val matchingPoints = dao.findWifiPoints(scanResults)

        if (matchingPoints.isNotEmpty()) {
            onStationFound(matchingPoints.first().station)
            toast("${matchingPoints.map { it.station }}")

        } else {
            mainProgressBar.visibility = View.GONE
            longToast("No matching points: $scanResults")
        }
    }

    protected fun attemptScan() { // auto try
        log("attemptScan")

        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            scanViewModel.getWifiScanResults()
        }
    }


}