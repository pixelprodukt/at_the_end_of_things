package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.ateot.components.HitpointsComponent
import com.mygdx.ateot.components.PlayerComponent
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.helper.GameContext
import ktx.graphics.use

class GameinfoDebugRenderingSystem(context: GameContext, private val uiBatch: SpriteBatch) : EntitySystem() {

    private val uiCamera = OrthographicCamera(800f / 3f, 600f / 3f)
    private val inputHandler = context.inputHandler
    private val engine = context.engine
    private val font = context.assetHandler.assets.get<BitmapFont>(Assets.FONT)

    init {
        uiCamera.position.x = 130.0f
        uiCamera.position.y = -96.0f
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (inputHandler.isDebug) {

            uiCamera.update()
            uiBatch.projectionMatrix = uiCamera.combined

            val playerFamily = Family.all(PlayerComponent::class.java).get()
            val playerEntity = engine.getEntitiesFor(playerFamily)
            val hitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java).get(playerEntity.first())

            uiBatch.use { spriteBatch ->
                font.draw(spriteBatch, "FPS: ${Gdx.graphics.framesPerSecond}\nPlayer Hitpoints: ${hitpointsComponent.hitpoints}", 0.0f, 0.0f)
            }
        }

    }
}