package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.mygdx.ateot.enums.BulletType

class WeaponComponent : Component {
    var bulletType: BulletType = BulletType.NONE
    var fireRate: Float = 0.0f
    var muzzle: Entity? = null
}