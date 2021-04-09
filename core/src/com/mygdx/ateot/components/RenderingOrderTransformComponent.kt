package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class RenderingOrderTransformComponent : Component, Pool.Poolable {

    val position = Vector3(0.0f, 0.0f, 0.0f)
    val offset = Vector3(0.0f, 0.0f, 0.0f)
    var syncWithTransform = true

    override fun reset() {
        position.set(Vector3(0.0f, 0.0f, 0.0f))
        offset.set(Vector3(0.0f, 0.0f, 0.0f))
        syncWithTransform = true
    }
}