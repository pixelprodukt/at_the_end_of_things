package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.mygdx.ateot.components.*
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.events.CreateExplosionEvent
import com.mygdx.ateot.helper.*

class CollisionSystem(context: GameContext, private val entityFactory: EntityFactory) :
    IteratingSystem(Family.all(BodyComponent::class.java).get()) {

    private val mapHandler = context.mapHandler
    private val eventHandler = context.eventHandler

    private val staticCollisionQueue = mutableListOf<Entity>()
    private val dynamicCollisionQueue = mutableListOf<Entity>()
    private val sensorCollisionQueue = mutableListOf<Entity>()
    private val wallCollisionQueue = mutableListOf<Entity>()
    private val floorCollisionQueue = mutableListOf<Entity>()
    private val bulletCollisionQueue = mutableListOf<Entity>()

    private val mapperBodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        /**
         * collision for movable objects with static mapbodies
         */
        staticCollisionQueue.forEach { staticCollisionEntity ->

            val staticBody = mapperBodyComponent.get(staticCollisionEntity).body

            dynamicCollisionQueue.forEach { dynamicCollisionEntity ->

                val dynamicBody = mapperBodyComponent.get(dynamicCollisionEntity).body
                val dynamicTransformComponent = mapperTransformComponent.get(dynamicCollisionEntity)

                resolveCollision(dynamicBody, staticBody)

                dynamicTransformComponent.position.x = dynamicBody.position.x
                dynamicTransformComponent.position.y = dynamicBody.position.y

                /**
                 * Check if entity has the PlayerComponent and set transform of weapon entity to body's transform
                 */
                if (mapperPlayerComponent.get(dynamicCollisionEntity) != null) {
                    val playerComponent = mapperPlayerComponent.get(dynamicCollisionEntity)

                    if (playerComponent.weapon != null) {
                        val weaponTransform = mapperTransformComponent.get(playerComponent.weapon)

                        weaponTransform.position.x = dynamicBody!!.position.x
                        weaponTransform.position.y = dynamicBody!!.position.y
                    }
                }
            }
        }

        /**
         * bulletcollisions are handled here
         */
        bulletCollisionQueue.forEach { bulletEntity ->

            wallCollisionQueue.forEach { wallCollisionEntity ->

                val staticBody = mapperBodyComponent.get(wallCollisionEntity).body
                val bulletBody = mapperBodyComponent.get(bulletEntity).body
                val bulletComponent = mapperBulletComponent.get(bulletEntity)
                val transformComponent = mapperTransformComponent.get(bulletEntity)

                if (intersect(bulletBody, staticBody)) {
                    val eventData =
                        CreateExplosionEventData(bulletComponent.explosionType, transformComponent.position)
                    eventHandler.publish(CreateExplosionEvent(eventData))
                    entityFactory.removeFromEngine(bulletEntity)
                }

                transformComponent.position.x = bulletBody.position.x
                transformComponent.position.y = bulletBody.position.y
            }
        }

        staticCollisionQueue.clear()
        dynamicCollisionQueue.clear()
        sensorCollisionQueue.clear()
        wallCollisionQueue.clear()
        floorCollisionQueue.clear()
        bulletCollisionQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        if (entity != null) {

            val bodyComponent = mapperBodyComponent.get(entity)
            val bulletComponent = mapperBulletComponent.get(entity)

            if (bodyComponent.isCollision && bodyComponent.isStatic && bodyComponent.isHitable) {
                staticCollisionQueue.add(entity)
                wallCollisionQueue.add(entity)
            }

            if (bodyComponent.isCollision && bodyComponent.isStatic && !bodyComponent.isHitable) {
                staticCollisionQueue.add(entity)
                floorCollisionQueue.add(entity)
            }

            if (bodyComponent.isCollision && !bodyComponent.isStatic && !bodyComponent.isHitable) {
                dynamicCollisionQueue.add(entity)
            }

            if (!bodyComponent.isCollision && bulletComponent != null) {
                bulletCollisionQueue.add(entity)
                //Gdx.app.log("CollisionSystem", "Bullet added: ${entity}")
            }
        }

    }
}