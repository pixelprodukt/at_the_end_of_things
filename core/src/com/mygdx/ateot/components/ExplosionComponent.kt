package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ExplosionComponent : Component, Pool.Poolable {

    var damage = 0

    override fun reset() {
        damage = 0
    }
}