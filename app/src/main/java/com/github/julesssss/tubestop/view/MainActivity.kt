package com.github.julesssss.tubestop.view

import android.os.Bundle
import android.view.View
import com.github.julesssss.tubestop.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ScanActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mainButtonScan.setOnClickListener {
            attemptScan()
            mainProgressBar?.visibility = View.VISIBLE
        }
    }

    override fun onStationFound(station: String) {

        mainProgressBar?.visibility = View.GONE

        mainTextStation?.text = station
    }
}