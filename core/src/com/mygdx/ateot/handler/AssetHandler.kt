package com.mygdx.ateot.handler

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.helper.AnimationFactory
import kotlin.random.Random

class AssetHandler {

    val animationHelper = AnimationFactory()

    val assets = AssetManager().apply {

        load(Assets.PLAYER, Texture::class.java)
        load(Assets.PLAYER_DEATH, Texture::class.java)
        load(Assets.WEAPON_HANDS, Texture::class.java)
        load(Assets.WEAPON_RIFLE, Texture::class.java)
        load(Assets.WEAPON_ROCKETLAUNCHER, Texture::class.java)
        load(Assets.BULLET_EXPLOSION, Texture::class.java)
        load(Assets.ROCKET_EXPLOSION, Texture::class.java)
        load(Assets.BARREL_EXPLOSION, Texture::class.java)

        load(Assets.ENEMY_FLESHBLOB, Texture::class.java)
        load(Assets.EXPLOSIVE_BARRELS, Texture::class.java)

        val resolver = InternalFileHandleResolver()
        setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        setLoader(BitmapFont::class.java,".ttf", FreetypeFontLoader(resolver))

        val fontLoaderParameter = FreetypeFontLoader.FreeTypeFontLoaderParameter()
        fontLoaderParameter.fontFileName = Assets.FONT
        fontLoaderParameter.fontParameters.size = 7//16
        fontLoaderParameter.fontParameters.spaceY = -6//-2

        load(Assets.FONT, BitmapFont::class.java, fontLoaderParameter)

        finishLoading()
    }

    val rifleshotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shot_01.wav"))
    val explosion01 = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion_01.wav"))
    val fleshblobDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/fleshblob_death.wav"))
    val playerDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/player_death.wav"))
    val playerGotHit = Gdx.audio.newSound(Gdx.files.internal("sounds/player_got_hit.wav"))
    val hitsound01 = Gdx.audio.newSound(Gdx.files.internal("sounds/hitsound_01.wav"))
    val stepSounds = arrayOf(
        Gdx.audio.newSound(Gdx.files.internal("sounds/step_01.wav")),
        Gdx.audio.newSound(Gdx.files.internal("sounds/step_02.wav")),
        Gdx.audio.newSound(Gdx.files.internal("sounds/step_03.wav"))
    )

    fun getRandomStepSound(): Sound {
        return stepSounds[Random.nextInt(0, 3)]
    }
}