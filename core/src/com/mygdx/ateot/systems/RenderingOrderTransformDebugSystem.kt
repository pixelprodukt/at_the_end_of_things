package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.ateot.components.CollisionBodyComponent
import com.mygdx.ateot.components.RenderingOrderTransformComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.helper.GameContext
import ktx.graphics.use

class RenderingOrderTransformDebugSystem(
    context: GameContext,
    private val camera: OrthographicCamera
) : EntitySystem() {

    private val inputHandler = context.inputHandler
    private val shapeRenderer = ShapeRenderer()
    private val mapperRenderingOrderTransformComponent = ComponentMapper.getFor(RenderingOrderTransformComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val renderingTransformsFamily = Family.all(RenderingOrderTransformComponent::class.java, TransformComponent::class.java).get()
        val renderingTransformsEntities = engine.getEntitiesFor(renderingTransformsFamily)

        if (inputHandler.isDebug) {

            shapeRenderer.projectionMatrix = camera.combined

            shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
                shapeRenderer.color = Color.CYAN

                renderingTransformsEntities.forEach { entity ->

                    val renderingOrderTransformComponent = mapperRenderingOrderTransformComponent.get(entity)

                    if (renderingOrderTransformComponent != null) {

                        shapeRenderer.line(
                            (renderingOrderTransformComponent.position.x + renderingOrderTransformComponent.offset.x) - 2,
                            (renderingOrderTransformComponent.position.y + renderingOrderTransformComponent.offset.y) - 0,
                            (renderingOrderTransformComponent.position.x + renderingOrderTransformComponent.offset.x) + 2,
                            (renderingOrderTransformComponent.position.y + renderingOrderTransformComponent.offset.y) + 0
                        )

                        shapeRenderer.line(
                            (renderingOrderTransformComponent.position.x + renderingOrderTransformComponent.offset.x) - 0,
                            (renderingOrderTransformComponent.position.y + renderingOrderTransformComponent.offset.y) - 2,
                            (renderingOrderTransformComponent.position.x + renderingOrderTransformComponent.offset.x) + 0,
                            (renderingOrderTransformComponent.position.y + renderingOrderTransformComponent.offset.y) + 2
                        )
                    }
                }
            }
        }
    }
}