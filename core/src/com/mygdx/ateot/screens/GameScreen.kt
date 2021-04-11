package com.mygdx.ateot.screens

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.constants.WeaponConfig
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.WeaponType
import com.mygdx.ateot.helper.EntityFactory
import com.mygdx.ateot.helper.GameContext
import com.mygdx.ateot.systems.*

class GameScreen : Screen {

    private val context = GameContext()
    private val batch = SpriteBatch()
    private val uiBatch = SpriteBatch()
    private val renderingSystem = RenderingSystem(batch)
    private val camera = renderingSystem.camera
    private val engine = context.engine
    private val mapRenderer = OrthogonalTiledMapRenderer(context.mapHandler.currentTiledMap)
    private var playerEntity: ImmutableArray<Entity>?
    private var playerTransformComponent: TransformComponent?
    private val entityFactory = context.entityFactory // EntityFactory(engine, context)

    private val pixmap = Pixmap(Gdx.files.internal("cursor.png"))
    private val xHotspot = (pixmap.width / 2)
    private val yHotspot = (pixmap.height / 2)

    private val cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot)

    init {
        Gdx.graphics.setCursor(cursor)

        batch.projectionMatrix = camera.combined

        engine.addSystem(AnimationSystem())
        engine.addSystem(renderingSystem)
        engine.addSystem(BodyDebugRenderingSystem(context, camera))
        engine.addSystem(RenderingOrderTransformDebugSystem(context, camera))
        engine.addSystem(TransformDebugRenderingSystem(context, camera))
        engine.addSystem(ExplosionSystem(context, entityFactory))

        engine.addSystem(PlayerControlSystem(context, camera))
        engine.addSystem(CollisionSystem(context, entityFactory))
        engine.addSystem(WeaponSystem(camera))
        engine.addSystem(BulletSystem(context, entityFactory, camera))
        engine.addSystem(SyncTransformAndBodiesSystem(context))
        engine.addSystem(SyncRenderingOrderTransformWithTransformSystem())
        engine.addSystem(HitpointsSystem(context))
        engine.addSystem(GameinfoDebugRenderingSystem(context, uiBatch))

        Gdx.input.inputProcessor = context.inputHandler

        initEntities(engine)

        mapRenderer.map = context.mapHandler.currentTiledMap
        mapRenderer.setView(camera)

        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
        playerTransformComponent = ComponentMapper.getFor(TransformComponent::class.java).get(playerEntity?.first())
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

    fun initEntities(engine: PooledEngine) {
        val player = entityFactory.createPlayer()
        val rifle = entityFactory.createWeapon(WeaponConfig.valuesFor[WeaponType.RIFLE]!!)
        val rocketlauncher = entityFactory.createWeapon(WeaponConfig.valuesFor[WeaponType.ROCKETLAUNCHER]!!)

        player.getComponent(PlayerComponent::class.java).weapon = rifle

        engine.addEntity(player)
        engine.addEntity(rifle)
        engine.addEntity(rocketlauncher)
    }

    /**
     * Utility function
     */
    private fun clamp(value: Float, max: Float, min: Float): Float {
        return if (value > min) {
            if (value < max) {
                value
            } else max
        } else min
    }
}