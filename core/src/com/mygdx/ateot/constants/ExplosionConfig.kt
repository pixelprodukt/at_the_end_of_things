package com.mygdx.ateot.constants

import com.badlogic.gdx.math.Vector2
import com.mygdx.ateot.data.ExplosionConfigData
import com.mygdx.ateot.enums.ExplosionType

object ExplosionConfig {

    val valuesFor: HashMap<ExplosionType, ExplosionConfigData> = hashMapOf()

    init {
        valuesFor[ExplosionType.BULLET] = ExplosionConfigData(110, Vector2(10.0f, 10.0f), 16, Assets.BULLET_EXPLOSION)
        valuesFor[ExplosionType.ROCKET] = ExplosionConfigData(100, Vector2(32.0f, 32.0f), 32, Assets.ROCKET_EXPLOSION)
        valuesFor[ExplosionType.BARREL] = ExplosionConfigData(80, Vector2(48.0f, 48.0f), 64, Assets.BARREL_EXPLOSION)
    }
}