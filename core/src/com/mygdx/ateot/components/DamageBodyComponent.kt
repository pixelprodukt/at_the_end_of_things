package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.mygdx.ateot.helper.Body

class DamageBodyComponent : Component, Pool.Poolable {

    val body = Body(Vector2(0.0f, 0.0f), Vector2(0.0f, 0.0f), Vector2(0.0f, 0.0f));
    var damage = 0
    var isActive = false

    override fun reset() {
        body.position.set(0.0f, 0.0f)
        body.size.set(16.0f, 16.0f)
        body.velocity.set(0.0f, 0.0f)
        body.offset.set(0.0f, 0.0f)
        damage = 0
        isActive = false
    }
}