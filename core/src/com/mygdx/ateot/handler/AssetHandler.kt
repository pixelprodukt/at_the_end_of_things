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
        load(Assets.RIFLE_MUZZLE_BULLET, Texture::class.java)
        load(Assets.RIFLE_EXPLOSION, Texture::class.java)

        finishLoading()
    }
}