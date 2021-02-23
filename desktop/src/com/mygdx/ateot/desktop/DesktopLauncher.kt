package com.mygdx.ateot.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.mygdx.ateot.AtTheEndOfThings

fun main() {

    val config = LwjglApplicationConfiguration().apply {
        width = 800
        height = 600
        resizable = false
    }

    LwjglApplication(AtTheEndOfThings(), config)
}