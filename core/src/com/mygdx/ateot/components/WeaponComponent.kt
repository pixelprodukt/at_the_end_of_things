package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import com.mygdx.ateot.enums.BulletType

class WeaponComponent : Component, Pool.Poolable {

    var bulletType: BulletType = BulletType.NONE
    var fireRate: Float = 0.0f
    var muzzle: Entity? = null

    override fun reset() {
        bulletType = BulletType.NONE
        fireRate = 0.0f
        muzzle = null
    }
}