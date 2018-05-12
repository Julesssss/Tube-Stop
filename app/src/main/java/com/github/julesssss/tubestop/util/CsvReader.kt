package com.github.julesssss.tubestop.util

import android.content.Context
import com.github.julesssss.tubestop.data.WifiPoint
import java.io.BufferedReader
import java.io.InputStreamReader


class CsvReader() {

    fun getWifiPointsFromCSV(context: Context): ArrayList<WifiPoint> {

        val assets = context.assets
        val isr = InputStreamReader(assets.open("points.csv"))

        val reader = BufferedReader(isr)
        reader.readLine()

        var line: String? = ""
        val points = arrayListOf<WifiPoint>()

        while (line != null) {
            line = reader.readLine()
            line?.let {
                println(line)
                val i = it.indexOf(",")
                val st = it.substring(0, i)
                val po = it.substring((i + 1), line.length)

                points.add(WifiPoint(po, st))
            }
        }


        return points
    }
}
