package com.example.weather.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class CityInfo(

    @PrimaryKey
    var id: String = "",

    @Required
    var name: String = ""

): RealmObject()