package com.example.weather.db

import io.realm.RealmConfiguration

object DbConfig {

    private val realmVersion = 2L
    private val dbName = "cities_db.db"

    fun provideRealmConfig(): RealmConfiguration =
        RealmConfiguration.Builder()
            .name(dbName)
            .schemaVersion(realmVersion)
            .deleteRealmIfMigrationNeeded()
            .build()

}