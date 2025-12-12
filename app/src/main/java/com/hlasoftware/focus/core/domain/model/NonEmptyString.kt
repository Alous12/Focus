package com.hlasoftware.focus.core.domain.model

@JvmInline
value class NonEmptyString private constructor(val value: String) {

    companion object {
        fun create(raw: String): NonEmptyString {
            require(raw.isNotBlank()) { "El valor no puede estar vacío" }
            return NonEmptyString(raw.trim())
        }
    }

    override fun toString(): String = value
}