package com.example.weather.db

import io.realm.RealmConfiguration

object DbConfig {

    private const val realmVersion = 2L
    private const val dbName = "cities_db.db"

    fun provideRealmConfig(): RealmConfiguration =
        RealmConfiguration.Builder()
            .name(dbName)
            .schemaVersion(realmVersion)
            .deleteRealmIfMigrationNeeded()
            .build()

}