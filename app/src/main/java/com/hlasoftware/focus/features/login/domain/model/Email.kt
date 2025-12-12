package com.hlasoftware.focus.features.login.domain.model

//    CALYR.software@gmail.com OK  Calyr.software@gmail.com OK  calyr.software@gmail.com
//    calyr.software@gmail.com     calyr.software@gmail.com
// [         calyr.     software@#__-=..com       ]
//     a@a.a
@JvmInline
value class Email private constructor(val value: String) {

    companion object {
        fun create(raw: String): Email {
            require(raw.isNotEmpty()) {
                "Email must not be empty"
            }

            val normalized = raw.trim().lowercase()
            require(raw.contains("@")) {
                "Email must contain '@'"
            }

            return Email(normalized)
        }
    }

    override fun toString(): String = value
}