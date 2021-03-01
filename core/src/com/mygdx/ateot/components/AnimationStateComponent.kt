package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.Gdx

class AnimationStateComponent : Component {

    var state: Int? = null
        set(value) {
            // Prevent permanent setter calls in update when old state is the same as the new state
            if (field != value) {
                field = value
                time = 0.0f
            }
        }

    var time: Float = 0.0f
    var isLooping: Boolean = false

    companion object {
        const val IDLE_DOWN_LEFT = 0
        const val IDLE_DOWN_RIGHT = 1
        const val IDLE_UP_LEFT = 2
        const val IDLE_UP_RIGHT = 3
        const val MOVE_DOWN_LEFT = 4
        const val MOVE_DOWN_RIGHT = 5
        const val MOVE_UP_LEFT = 6
        const val MOVE_UP_RIGHT = 7
        const val WEAPON_ORIENTATION_LEFT = 8
        const val WEAPON_ORIENTATION_RIGHT = 9
        const val WEAPON_MUZZLE = 10
    }
}