package com.mygdx.ateot.screens

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.handler.AssetHandler
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.systems.*

class GameScreen : Screen {

    private val assetHandler = AssetHandler()
    private val inputHandler = InputHandler()
    private val batch = SpriteBatch()

    private val renderingSystem = RenderingSystem(batch)
    private val camera = renderingSystem.camera

    private val engine = PooledEngine()

    private val mapHandler = MapHandler().apply { loadMap("ateot_testmap") }
    private val mapRenderer = OrthogonalTiledMapRenderer(mapHandler.currentTiledMap)

    private var playerEntity: ImmutableArray<Entity>?
    private var playerTransformComponent: TransformComponent?

    init {
        batch.projectionMatrix = camera.combined

        engine.addSystem(AnimationSystem())
        //engine.addSystem(CollisionSystem(mapHandler))

        engine.addSystem(renderingSystem)
        engine.addSystem(ShapeRenderingSystem(mapHandler, inputHandler, camera))

        engine.addSystem(WeaponSystem(camera))
        engine.addSystem(PlayerControlSystem(inputHandler, camera))
        engine.addSystem(CollisionSystem(mapHandler))

        Gdx.input.inputProcessor = inputHandler

        engine.addEntity(createPlayer())

        mapRenderer.map = mapHandler.currentTiledMap
        mapRenderer.setView(camera)

        playerEntity = engine.getEntitiesFor(Family.all(PlayerComponent::class.java).get())
        playerTransformComponent = ComponentMapper.getFor(TransformComponent::class.java).get(playerEntity?.first())
    }

    /*private fun <T> classCreator(classType: Class<T>): T {
        return classType.newInstance();
    }*/

    private fun createPlayer(): Entity {
        val entity = engine.createEntity()
        val transform = engine.createComponent(TransformComponent::class.java)
        val body = engine.createComponent(BodyComponent::class.java)
        val texture = engine.createComponent(TextureComponent::class.java)
        val player = engine.createComponent(PlayerComponent::class.java)
        val animation = engine.createComponent(AnimationComponent::class.java)
        val animationState = engine.createComponent(AnimationStateComponent::class.java)

        body.body.position.x = 100f
        body.body.position.y = 60f

        body.body.size.set(8f, 13f)
        body.body.offset.set(-4f, -7f)

        animation.animations[AnimationStateComponent.IDLE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 0, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 2, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 3, 16, 1, 0.1f)

        animation.animations[AnimationStateComponent.MOVE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 0, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 2, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 3, 16, 4, 0.15f)

        animationState.state = AnimationStateComponent.MOVE_DOWN_RIGHT
        animationState.isLooping = true

        val playerTex: Texture = assetHandler.assets.get(Assets.PLAYER)
        texture.region = TextureRegion(playerTex)

        player.weapon = createWeapon()

        engine.addEntity(player.weapon)

        entity.add(transform)
        entity.add(body)
        entity.add(texture)
        entity.add(player)
        entity.add(animation)
        entity.add(animationState)

        return entity
    }

    private fun createWeapon(): Entity {
        val entity = engine.createEntity()
        val transform = engine.createComponent(TransformComponent::class.java)
        val animation = engine.createComponent(AnimationComponent::class.java)
        val animationState = engine.createComponent(AnimationStateComponent::class.java)
        val texture = engine.createComponent(TextureComponent::class.java)
        val weapon = engine.createComponent(WeaponComponent::class.java)

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_RIGHT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.WEAPON_RIFLE), 0, 0, 16, 1, 0.2f)

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_LEFT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.WEAPON_RIFLE), 16, 0, 16, 1, 0.2f)

        animationState.state = AnimationStateComponent.WEAPON_ORIENTATION_RIGHT
        animationState.isLooping = true

        transform.position.z = 0.1f

        entity.add(transform)
        entity.add(animation)
        entity.add(animationState)
        entity.add(texture)
        entity.add(weapon)

        return entity
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
        camera.position.x = clamp(camera.position.x, mapHandler.currentMapWidth!!.toFloat() - (camera.viewportWidth / 2), 0 + (camera.viewportWidth / 2))
        camera.position.y = clamp(camera.position.y, mapHandler.currentMapHeight!!.toFloat() - (camera.viewportHeight / 2), 0 + (camera.viewportHeight / 2))

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