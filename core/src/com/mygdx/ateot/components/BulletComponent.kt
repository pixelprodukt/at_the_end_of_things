package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.enums.BulletType

class BulletComponent : Component {
    var type = BulletType.NONE
    var speed = 5.0f
    var maxLifetime = 1.0f
    var timeAlive = 0.0f
    var spawn = Vector3()
    var target = Vector3()
}