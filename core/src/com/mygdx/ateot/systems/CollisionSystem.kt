package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.mygdx.ateot.components.*
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.events.CreateExplosionEvent
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.helper.*

class CollisionSystem(context: GameContext, private val entityFactory: EntityFactory) :
    IteratingSystem(Family.all(BodyComponent::class.java).get()) {

    private val mapHandler = context.mapHandler
    private val eventHandler = context.eventHandler
    private val collisionQueue = mutableListOf<Entity>()
    private val mapperBodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        collisionQueue.forEach { entity ->
            mapHandler.staticMapBodies.forEach { body ->

                val entityBody = mapperBodyComponent.get(entity)?.body
                val entityTransformComponent = mapperTransformComponent.get(entity)

                if (entityBody != null) {

                    if (!entityBody.isSensor) {
                        resolveCollision(entityBody, body)
                    }

                    if (mapperBulletComponent.get(entity) != null) {

                        val bulletComponent = mapperBulletComponent.get(entity)
                        val transformComponent = mapperTransformComponent.get(entity)

                        if (intersect(entityBody, body)) {
                            val eventData = CreateExplosionEventData(bulletComponent.explosionType, transformComponent.position)
                            eventHandler.publish(CreateExplosionEvent(eventData))
                            entityFactory.removeFromEngine(entity)
                        }
                    }

                    entityTransformComponent.position.x = entityBody.position.x
                    entityTransformComponent.position.y = entityBody.position.y
                }

                /**
                 * Check if entity has the PlayerComponent and set transform of weapon entity to body's transform
                 */
                if (mapperPlayerComponent.get(entity) != null) {
                    val playerComponent = mapperPlayerComponent.get(entity)

                    if (playerComponent.weapon != null) {
                        val weaponTransform = mapperTransformComponent.get(playerComponent.weapon)

                        weaponTransform.position.x = entityBody!!.position.x
                        weaponTransform.position.y = entityBody!!.position.y
                    }
                }
            }
        }

        collisionQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        collisionQueue.add(entity!!)
    }
}