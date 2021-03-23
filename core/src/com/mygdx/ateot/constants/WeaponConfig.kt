package com.mygdx.ateot.constants

import com.mygdx.ateot.data.WeaponConfigData
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.WeaponType

object WeaponConfig {

    val valuesFor: HashMap<WeaponType, WeaponConfigData> = hashMapOf()

    init {
        valuesFor[WeaponType.NONE] = WeaponConfigData(WeaponType.NONE, BulletType.NONE, 0.7f, "")
        valuesFor[WeaponType.RIFLE] = WeaponConfigData(WeaponType.RIFLE, BulletType.RIFLE_BULLET, 0.15f, Assets.WEAPON_RIFLE)
        valuesFor[WeaponType.ROCKETLAUNCHER] = WeaponConfigData(WeaponType.ROCKETLAUNCHER, BulletType.ROCKET, 0.8f, Assets.WEAPON_ROCKETLAUNCHER)
    }
}