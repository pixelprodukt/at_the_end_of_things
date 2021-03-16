package com.mygdx.ateot.constants

import com.mygdx.ateot.data.BulletConfigData
import com.mygdx.ateot.enums.BulletType

object BulletConfig {

    val valuesFor: HashMap<BulletType, BulletConfigData> = hashMapOf()

    init {
        valuesFor[BulletType.RIFLE_BULLET] = BulletConfigData(5.0f, 5.0f)
        valuesFor[BulletType.ROCKET] = BulletConfigData(1.5f, 10.0f)
    }
}

