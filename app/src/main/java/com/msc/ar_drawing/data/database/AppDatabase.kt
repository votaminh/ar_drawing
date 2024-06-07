package com.msc.ar_drawing.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msc.ar_drawing.component.details.DetailsSound
import com.msc.ar_drawing.data.database.dao.FavouriteDao

@Database(entities = arrayOf(DetailsSound::class), version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "ringtone"
    }

    abstract fun favouriteDao() : FavouriteDao
}