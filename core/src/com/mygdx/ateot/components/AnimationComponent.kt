package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimationComponent : Component {
    val animations: HashMap<Int, Animation<TextureRegion>> = HashMap()
}
