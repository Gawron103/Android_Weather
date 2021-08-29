package com.example.weather.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_data_table")
data class City(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "city_id")
    val id: Int,

    @ColumnInfo(name = "city_name")
    val name: String

)