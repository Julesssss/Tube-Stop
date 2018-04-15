package com.github.julesssss.tubestop.data

import android.arch.persistence.room.*

@Dao
interface WifiPointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertWifiPoints(wifiPoints: List<WifiPoint>)

    @Query("SELECT * FROM wifi_points")
    fun findAllWifiPoints(): List<WifiPoint>

    @Query("SELECT * FROM wifi_points WHERE station == :station")
    fun findWifiPointsForStation(station: String): List<WifiPoint>

    @Query("SELECT * FROM wifi_points WHERE bssid == :bssid LIMIT 1")
    fun findWifiPoint(bssid: String): WifiPoint

    @Query("SELECT * FROM wifi_points WHERE bssid IN (:bssid)")
    fun findWifiPoints(bssid: List<String>): List<WifiPoint>

    @Delete
    fun deleteWifiPoints(wifiPoints: List<WifiPoint>)

}