package com.hlasoftware.focus.features.login.domain.model

@JvmInline
value class Email private constructor(val value: String) {

    companion object {
        fun create(raw: String): Email {
            require(raw.isNotEmpty()) {
                "Email no puede estar vacio"
            }

            val normalized = raw.trim().lowercase()
            require(normalized.contains("@")) {
                "Email debe contener '@'"
            }
            require(normalized.endsWith(".com")) {
                "Email debe terminar '.com'"
            }

            return Email(normalized)
        }
    }

    override fun toString(): String = value
}