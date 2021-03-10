package com.mygdx.ateot.screens

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.mygdx.ateot.components.*
import com.mygdx.ateot.handler.AssetHandler
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.helper.EntityBuilder
import com.mygdx.ateot.helper.GameContext
import com.mygdx.ateot.systems.*

class GameScreen : Screen {

    private val context = GameContext()
    //private val assetHandler = AssetHandler()
    //private val inputHandler = InputHandler()
    private val batch = SpriteBatch()

    private val renderingSystem = RenderingSystem(batch)
    private val camera = renderingSystem.camera

    private val engine = PooledEngine()

    //private val mapHandler = MapHandler().apply { loadMap("ateot_testmap") }
    private val mapRenderer = OrthogonalTiledMapRenderer(context.mapHandler.currentTiledMap)

    private var playerEntity: ImmutableArray<Entity>?
    private var playerTransformComponent: TransformComponent?

    private val entityFactory = EntityBuilder(engine, context)

    init {
        batch.projectionMatrix = camera.combined

        engine.addSystem(AnimationSystem())
        engine.addSystem(renderingSystem)
        engine.addSystem(BodyDebugRenderingSystem(context, camera))
        engine.addSystem(TransformDebugRenderingSystem(context, camera))
        engine.addSystem(PlayerControlSystem(context, camera))
        engine.addSystem(WeaponSystem(camera))
        engine.addSystem(CollisionSystem(context))
        engine.addSystem(BulletSystem(context, entityFactory, camera))
        engine.addSystem(ExplosionSystem(context, entityFactory))

        Gdx.input.inputProcessor = context.inputHandler

        engine.addEntity(entityFactory.createPlayer())

        mapRenderer.map = context.mapHandler.currentTiledMap
        mapRenderer.setView(camera)

        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
        playerTransformComponent = ComponentMapper.getFor(TransformComponent::class.java).get(playerEntity?.first())
    }

    private fun clamp(value: Float, max: Float, min: Float): Float {
        return if (value > min) {
            if (value < max) {
                value
            } else max
        } else min
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(120f / 255f, 97f / 255f, 114f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(camera)
        camera.position.set(playerTransformComponent!!.position.x, playerTransformComponent!!.position.y, 0f)
        camera.position.x = clamp(camera.position.x, context.mapHandler.currentMapWidth!!.toFloat() - (camera.viewportWidth / 2), 0 + (camera.viewportWidth / 2))
        camera.position.y = clamp(camera.position.y, context.mapHandler.currentMapHeight!!.toFloat() - (camera.viewportHeight / 2), 0 + (camera.viewportHeight / 2))

        mapRenderer.render()

        engine.update(delta)
    }

    override fun show() {}

    override fun resize(width: Int, height: Int) {}

    override fun pause() {}

    override fun resume() {}

    override fun hide() {}

    override fun dispose() {}
}