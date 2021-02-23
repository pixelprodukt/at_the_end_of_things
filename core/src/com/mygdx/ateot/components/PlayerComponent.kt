package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.mygdx.ateot.enums.Direction

class PlayerComponent : Component {
    var direction: Direction = Direction.DOWN_RIGHT
    var isMoving: Boolean = false
    var weapon: Entity? = null
}