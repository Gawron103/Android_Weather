package com.example.weather.utils

object InputValidator {

    fun checkInput(input: String): Boolean {
        var result = checkIfNotEmpty(input)

        if (result) {
            result = checkIfNoNumbers(input)
        }

        if (result) {
            result = checkIfNoSigns(input)
        }

        return result
    }

    private fun checkIfNoNumbers(input: String): Boolean {
        return input.none { it.isDigit() }
    }

    private fun checkIfNoSigns(input: String): Boolean {
        return input.all { it.isLetter() }
    }

    private fun checkIfNotEmpty(input: String): Boolean {
        return input.isNotEmpty()
    }

}