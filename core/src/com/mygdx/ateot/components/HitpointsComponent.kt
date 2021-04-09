package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HitpointsComponent : Component, Pool.Poolable {

    var hitpoints = 1
    var isInvincible = false
    var invincibilityTimeAfterHit = 0.3f
    var timeSinceLastHit = 0.0f

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
        isInvincible = false
        invincibilityTimeAfterHit = 2.0f
        timeSinceLastHit = 0.0f
    }
}