package com.example.androidfi.airlines.dbWithRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidfi.airlines.AirlineOperator

@Database(entities = [ AirlineOperator::class ], version=6, exportSchema = false)
abstract class AppDatabase: RoomDatabase()
{
    public abstract fun groupOperatorDao(): AirlineOperatorDao
}