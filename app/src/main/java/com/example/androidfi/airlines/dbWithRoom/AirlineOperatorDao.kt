package com.example.androidfi.airlines.dbWithRoom

import androidx.room.*
import com.example.androidfi.airlines.AirlineOperator

@Dao
interface AirlineOperatorDao
{
    @Query("SELECT * FROM AirlineOperator")
    fun getAll(): List<AirlineOperator?>?

    @Query("SELECT * FROM AirlineOperator WHERE id = :id")
    fun getById(id: Int): AirlineOperator

    @Insert
    fun insert(go: AirlineOperator?)

    @Delete
    fun delete(go: AirlineOperator?)
}