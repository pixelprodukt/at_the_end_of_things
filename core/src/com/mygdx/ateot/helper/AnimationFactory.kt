package com.mygdx.ateot.helper

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimationFactory {

    fun createAnimation(
        textureSheet: Texture,
        startX: Int,
        startY: Int,
        framesize: Int,
        framecount: Int,
        frameDuration: Float
    ): Animation<TextureRegion> {

        val regionSheet = TextureRegion(textureSheet, startX, startY, framecount * framesize, framesize)
        val regionSheetFrames = arrayOfNulls<TextureRegion>(framecount)

        regionSheetFrames.forEachIndexed { index, it ->
            regionSheetFrames[index] = TextureRegion(regionSheet, index * framesize, 0, framesize, framesize)
        }

        return Animation<TextureRegion>(frameDuration, *regionSheetFrames)
    }
}