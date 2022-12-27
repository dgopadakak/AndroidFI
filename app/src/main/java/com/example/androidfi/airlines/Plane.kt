package com.example.androidfi.airlines

data class Plane(
    val model: String,
    val color: String,
    val num: Int,
    val factory: String,
    val productionDate: String,
    val seats: Int,
    val isCargo: Int,
    val comment: String
)
