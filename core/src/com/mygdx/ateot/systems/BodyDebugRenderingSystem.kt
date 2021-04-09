package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.ateot.components.CollisionBodyComponent
import com.mygdx.ateot.components.DamageBodyComponent
import com.mygdx.ateot.helper.GameContext
import ktx.graphics.use

class BodyDebugRenderingSystem(
    context: GameContext,
    private val camera: OrthographicCamera
) : EntitySystem() {

    private val inputHandler = context.inputHandler
    private val engine = context.engine
    private val shapeRenderer = ShapeRenderer()
    private val mapperCollisionBodyComponent = ComponentMapper.getFor(CollisionBodyComponent::class.java)
    private val mapperDamageBodyComponent = ComponentMapper.getFor(DamageBodyComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val collisionBodiesFamily = Family.all(CollisionBodyComponent::class.java).get()
        val collisionBodiesEntities = engine.getEntitiesFor(collisionBodiesFamily)
        val damageBodiesFamily = Family.all(DamageBodyComponent::class.java).get()
        val damageBodiesEntities = engine.getEntitiesFor(damageBodiesFamily)

        if (inputHandler.isDebug) {

            shapeRenderer.projectionMatrix = camera.combined

            shapeRenderer.use(ShapeRenderer.ShapeType.Line) {
                shapeRenderer.color = Color.GREEN

                collisionBodiesEntities.forEach { entity ->
                    shapeRenderer.color = Color.GREEN

                    val bodyComponent = mapperCollisionBodyComponent.get(entity)

                    if (bodyComponent != null) {

                        shapeRenderer.rect(
                            bodyComponent.body.position.x + bodyComponent.body.offset.x,
                            bodyComponent.body.position.y + bodyComponent.body.offset.y,
                            bodyComponent.body.size.x,
                            bodyComponent.body.size.y
                        )
                    }
                }

                damageBodiesEntities.forEach { entity ->
                    shapeRenderer.color = Color.BLUE
                    val bodyComponent = mapperDamageBodyComponent.get(entity)

                    if (bodyComponent != null) {

                        if (bodyComponent.isActive) {
                            shapeRenderer.color = Color.RED
                        }

                        shapeRenderer.rect(
                            bodyComponent.body.position.x + bodyComponent.body.offset.x,
                            bodyComponent.body.position.y + bodyComponent.body.offset.y,
                            bodyComponent.body.size.x,
                            bodyComponent.body.size.y
                        )
                    }
                }
            }
        }
    }

}