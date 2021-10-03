package com.example.weather.utils

object InputValidator {

    fun checkInput(input: String): Boolean {
        var result = checkIfNumbers(input)

        if (!result) {
            result = checkIfSigns(input)
        }

        if (!result) {
            result = checkIfEmpty(input)
        }

        return result
    }

    private fun checkIfNumbers(input: String): Boolean {
        return input.any { it.isDigit() }
    }

    private fun checkIfSigns(input: String): Boolean {
        return input.all { it.isLetter() }
    }

    private fun checkIfEmpty(input: String): Boolean {
        return input.isEmpty()
    }

}