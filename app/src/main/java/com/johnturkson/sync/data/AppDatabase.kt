package com.johnturkson.sync.data

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton

@Singleton
@Database(entities = [Code::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun codeDao(): CodeDao
}
