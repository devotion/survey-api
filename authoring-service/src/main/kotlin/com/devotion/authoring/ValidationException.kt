package com.devotion.authoring

class ValidationException(message: String) : RuntimeException(message) {
    companion object {
        private val serialVersionUID = 4329710056704148895L
    }
}
