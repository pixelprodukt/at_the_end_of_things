package com.mygdx.ateot.constants

import com.mygdx.ateot.data.BulletConfigData
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.ExplosionType

object BulletConfig {

    val valuesFor: HashMap<BulletType, BulletConfigData> = hashMapOf()

    init {
        valuesFor[BulletType.RIFLE_BULLET] = BulletConfigData(5, ExplosionType.BULLET, 5.0f, 5.0f, Assets.WEAPON_RIFLE)
        valuesFor[BulletType.ROCKET] = BulletConfigData(0, ExplosionType.ROCKET, 1.5f, 10.0f, Assets.WEAPON_ROCKETLAUNCHER)
    }
}

