package com.example.androidfi.airlines

data class Airline(
    val name: String,
    var listOfPlanes: ArrayList<Plane> = ArrayList()
)
