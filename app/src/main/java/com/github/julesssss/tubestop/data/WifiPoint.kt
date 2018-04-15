package com.github.julesssss.tubestop.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "wifi_points")
class WifiPoint() {


    @PrimaryKey
    var bssid :String = ""

    var ssid: String = ""

    var timestamp: Long = 0

    var station: String = ""

}