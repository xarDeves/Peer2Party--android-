package com.cups.splashin.peer2party.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [MessageDataClass::class], version = 1, exportSchema = false)
//@TypeConverters(DataConverter::class)
abstract class DataBaseHolder : RoomDatabase() {

    abstract fun dao(): DbDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DataBaseHolder? = null

        fun getInstance(context: Context): DataBaseHolder {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DataBaseHolder {
            return Room.databaseBuilder(context, DataBaseHolder::class.java, "entitiesDb")
                .build()
        }
    }

}
