package com.example.weather.utils

class InputValidator {

    fun checkInput(input: String): Boolean {
        var result = checkIfNumbers(input)

        if (!result) {
            result = checkIfSigns(input)
        }

        return result
    }

    private fun checkIfNumbers(input: String): Boolean {
        return input.any { it.isDigit() }

//        for (character in input) {
//            if(character.isDigit()) {
//                return true
//            }
//        }
//
//        return false
    }

    private fun checkIfSigns(input: String): Boolean {
        return input.all { it.isLetter() }
    }

}