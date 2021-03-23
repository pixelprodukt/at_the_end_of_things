package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.helper.GameContext
import ktx.graphics.use

class TransformDebugRenderingSystem(
    context: GameContext,
    private val camera: OrthographicCamera
) : IteratingSystem(Family.all(TransformComponent::class.java).get()) {

    private val inputHandler = context.inputHandler
    private val shapeRenderer = ShapeRenderer()
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val renderQueue = mutableListOf<Entity>()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        if (inputHandler.isDebug) {

            shapeRenderer.projectionMatrix = camera.combined

            shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
                shapeRenderer.color = Color.RED

                renderQueue.forEach { entity ->
                    val transformComponent = mapperTransformComponent.get(entity)

                    if (transformComponent != null) {

                        shapeRenderer.line(
                            (transformComponent.position.x + transformComponent.offset.x + transformComponent.originOffset.x) - 2,
                            (transformComponent.position.y + transformComponent.offset.y + transformComponent.originOffset.y) - 0,
                            (transformComponent.position.x + transformComponent.offset.x + transformComponent.originOffset.x) + 2,
                            (transformComponent.position.y + transformComponent.offset.y + transformComponent.originOffset.y) + 0
                        )

                        shapeRenderer.line(
                            (transformComponent.position.x + transformComponent.offset.x + transformComponent.originOffset.x) - 0,
                            (transformComponent.position.y + transformComponent.offset.y + transformComponent.originOffset.y) - 2,
                            (transformComponent.position.x + transformComponent.offset.x + transformComponent.originOffset.x) + 0,
                            (transformComponent.position.y + transformComponent.offset.y + transformComponent.originOffset.y) + 2
                        )
                    }
                }
            }
        }

        renderQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        renderQueue.add(entity!!)
    }
}