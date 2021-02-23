package com.mygdx.ateot

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.handler.AssetHandler
import com.mygdx.ateot.screens.GameScreen
import ktx.graphics.use
import kotlin.math.atan2
import kotlin.properties.Delegates

class AtTheEndOfThings : Game() {

    private lateinit var gameScreen: GameScreen

    override fun create() {
        gameScreen = GameScreen()
        setScreen(gameScreen)
    }

    override fun render() {
        gameScreen.render(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        gameScreen.dispose()
    }
}