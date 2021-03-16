package com.mygdx.ateot.data

import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.enums.BulletType

data class BulletDestroyedEventData(val bulletType: BulletType, val destroyedAtVector: Vector3)
