package com.github.julesssss.tubestop.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "wifi_points")
data class WifiPoint(@PrimaryKey var bssid: String = "", var station: String = "")