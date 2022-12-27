package com.example.androidfi.airlines.dbWithRoom

import androidx.room.TypeConverter
import com.example.androidfi.airlines.Airline
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AirlineOperatorConverter
{
    @TypeConverter
    fun fromGO(airlines: ArrayList<Airline>): String
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return gson.toJson(airlines)
    }

    @TypeConverter
    fun toGO(data: String): ArrayList<Airline>
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return try {
            val type: Type = object : TypeToken<ArrayList<Airline>>() {}.type
            gson.fromJson(data, type)
        } catch (e: Exception) {
            ArrayList()
        }
    }
}