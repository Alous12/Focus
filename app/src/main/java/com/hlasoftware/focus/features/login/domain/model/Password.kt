package com.hlasoftware.focus.features.login.domain.model

@JvmInline
value class Password private constructor(val value: String) {

    companion object {
        fun create(raw: String): Password {
            require(raw.length >= 6) {
                "La contraseña debe tener al menos 6 caracteres"
            }
            return Password(raw)
        }
    }

    override fun toString(): String = value
}