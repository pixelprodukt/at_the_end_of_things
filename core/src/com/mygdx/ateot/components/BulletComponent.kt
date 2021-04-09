package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.ExplosionType
import com.mygdx.ateot.helper.Body

class BulletComponent : Component, Pool.Poolable {

    var type = BulletType.NONE
    var explosionType = ExplosionType.BULLET
    var speed = 5.0f
    var maxLifetime = 1.0f
    var timeAlive = 0.0f
    var spawn = Vector3()
    var target = Vector3()

    override fun reset() {
        type = BulletType.NONE
        explosionType = ExplosionType.BULLET
        speed = 5.0f
        maxLifetime = 1.0f
        timeAlive = 0.0f
        spawn = Vector3()
        target = Vector3()
    }
}