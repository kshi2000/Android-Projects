package com.example.weatherapp.models

import java.io.Serializable

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Long,
    val humidity: Long,
    val sea_level: Long,
    val grnd_level: Long
):Serializable