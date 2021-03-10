package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.ateot.components.BodyComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.helper.GameContext
import ktx.graphics.use

class BodyDebugRenderingSystem(
    context: GameContext,
    private val camera: OrthographicCamera
) :
    IteratingSystem(Family.all(BodyComponent::class.java).get()) {

    private val mapHandler = context.mapHandler
    private val inputHandler = context.inputHandler
    private val shapeRenderer = ShapeRenderer()
    private val mapperBodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    private val renderQueue = mutableListOf<Entity>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (inputHandler.isDebug) {

            shapeRenderer.projectionMatrix = camera.combined

            shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
                shapeRenderer.color = Color.GREEN

                renderQueue.forEach { entity ->
                    shapeRenderer.color = Color.GREEN

                    val bodyComponent = mapperBodyComponent.get(entity)

                    shapeRenderer.rect(
                        bodyComponent.body.position.x + bodyComponent.body.offset.x,
                        bodyComponent.body.position.y + bodyComponent.body.offset.y,
                        bodyComponent.body.size.x,
                        bodyComponent.body.size.y
                    )
                }

                mapHandler.staticMapBodies.forEach { body ->
                    shapeRenderer.rect(body.position.x, body.position.y, body.size.x, body.size.y)
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        renderQueue.add(entity!!)
    }
}