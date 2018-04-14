package com.github.julesssss.tubestop.data

import android.arch.persistence.room.*

@Dao
interface WifiPointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWifiPoints(wifiPoints: List<WifiPoint>)

    @Query("SELECT * FROM wifi_points")
    fun findAllWifiPoints(): List<WifiPoint>

    @Query("SELECT * FROM wifi_points WHERE bssid == :bssid")
    fun findWifiPoint(bssid: String): WifiPoint

    @Delete
    fun deleteWifiPoints(wifiPoints: List<WifiPoint>)

}