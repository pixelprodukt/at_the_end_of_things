package com.mygdx.ateot.data

import com.mygdx.ateot.enums.ExplosionType

data class BulletConfigData(
    val damage: Int,
    val explosionType: ExplosionType,
    val speed: Float,
    val maxLifetime: Float
)
