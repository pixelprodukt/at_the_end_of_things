package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class AnimationComponent : Component, Pool.Poolable {

    val animations: HashMap<Int, Animation<TextureRegion>> = HashMap()

    override fun reset() {
        animations.clear()
    }
}
