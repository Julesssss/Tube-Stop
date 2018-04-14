package com.github.julesssss.tubestop.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [(WifiPoint::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wifiPointDao(): WifiPointDao
}