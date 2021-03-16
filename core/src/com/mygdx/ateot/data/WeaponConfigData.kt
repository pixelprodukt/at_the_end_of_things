package com.mygdx.ateot.data

import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.WeaponType

data class WeaponConfigData(
    val type: WeaponType,
    val bulletType: BulletType,
    val fireRate: Float,
    val assetPath: String
)
