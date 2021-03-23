package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.mygdx.ateot.helper.Body

class BodyComponent : Component, Pool.Poolable {

    val body: Body = Body(Vector2(0.0f, 0.0f), Vector2(16.0f, 16.0f), Vector2(0f, 0f), Vector2(0.0f, 0.0f))

    var isCollision = true
    var isStatic = true
    var isHitable = false

    override fun reset() {
        body.position.set(0.0f, 0.0f)
        body.size.set(16.0f, 16.0f)
        body.velocity.set(0.0f, 0.0f)
        body.offset.set(0.0f, 0.0f)
        isCollision = true
        isStatic = true
        isHitable = false
    }
}
