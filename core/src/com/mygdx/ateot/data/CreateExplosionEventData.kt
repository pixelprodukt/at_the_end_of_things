package com.mygdx.ateot.data

import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.enums.ExplosionType

data class CreateExplosionEventData(val explosionType: ExplosionType, val spawn: Vector3)
