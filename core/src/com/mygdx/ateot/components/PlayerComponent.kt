package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.mygdx.ateot.enums.Direction

class PlayerComponent : Component, Pool.Poolable {

    var direction: Direction = Direction.DOWN_RIGHT
    var isMoving: Boolean = false
    var weapon: Entity? = null

    override fun reset() {
        direction = Direction.DOWN_RIGHT
        isMoving = false
        weapon = null
    }
}