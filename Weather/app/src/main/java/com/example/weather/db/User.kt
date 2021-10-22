package com.example.weather.db

class User {

    lateinit var email: String
    lateinit var cities: List<String>

    constructor() {}

    constructor(email: String, cities: List<String>) {
        this.email = email
        this.cities = cities
    }

}