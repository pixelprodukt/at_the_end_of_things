package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HitpointsComponent : Component, Pool.Poolable {

    var hitpoints = 1

    val isAlive: Boolean
        get() {
            return hitpoints > 0
        }

    val isDead: Boolean
        get() {
            return hitpoints <= 0
        }

    override fun reset() {
        hitpoints = 1
    }
}