package com.mygdx.ateot.handler

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.helper.AnimationFactory

class AssetHandler {

    val animationHelper = AnimationFactory()

    val assets = AssetManager().apply {

        load(Assets.PLAYER, Texture::class.java)
        load(Assets.WEAPON_HANDS, Texture::class.java)
        load(Assets.WEAPON_RIFLE, Texture::class.java)
        load(Assets.WEAPON_ROCKETLAUNCHER, Texture::class.java)
        load(Assets.BULLET_EXPLOSION, Texture::class.java)
        load(Assets.ROCKET_EXPLOSION, Texture::class.java)
        load(Assets.BARREL_EXPLOSION, Texture::class.java)

        load(Assets.ENEMY_FLESHBLOB, Texture::class.java)
        load(Assets.EXPLOSIVE_BARRELS, Texture::class.java)

        finishLoading()
    }
}