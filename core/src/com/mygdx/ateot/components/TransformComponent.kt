package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent : Component, Pool.Poolable {

    val position: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    val offset: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    val originOffset: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    val scale: Vector2 = Vector2(1.0f, 1.0f)
    var rotation: Float = 0.0f
    var isHidden: Boolean = false

    override fun reset() {
        position.set(0.0f, 0.0f, 0.0f)
        offset.set(0.0f, 0.0f, 0.0f)
        originOffset.set(0.0f, 0.0f, 0.0f)
        scale.set(1.0f, 1.0f)
        rotation = 0.0f
        isHidden = false
    }
}