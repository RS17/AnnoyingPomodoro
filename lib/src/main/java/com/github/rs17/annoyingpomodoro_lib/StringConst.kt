package com.github.rs17.annoyingpomodoro_lib

enum class StringConst(val message: String) {
    // Universal strings should be here. Anything specific to a UI should be in the UI class.
    // Better to have strings in a specific class if possible
    POMS_THIS_SESSION("Pomodoros this session: "),
    POMS_TILL_LONG_BREAK("Pomodoros until long break: "),
    POM_RESUMED("Pomodoro resumed "),
    PAUSED("Paused ")
}

