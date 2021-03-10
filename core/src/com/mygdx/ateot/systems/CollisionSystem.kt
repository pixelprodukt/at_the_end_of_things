package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ateot.components.BodyComponent
import com.mygdx.ateot.components.PlayerComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.components.WeaponComponent
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.helper.GameContext
import com.mygdx.ateot.helper.resolveCollision

class CollisionSystem(context: GameContext) :
    IteratingSystem(Family.all(BodyComponent::class.java).get()) {

    private val mapHandler = context.mapHandler
    private val collisionQueue = mutableListOf<Entity>()
    private val mapperBodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        collisionQueue.forEach { entity ->
            mapHandler.staticMapBodies.forEach { body ->

                val entityBody = mapperBodyComponent.get(entity).body
                val entityTransformComponent = mapperTransformComponent.get(entity)

                resolveCollision(entityBody, body)

                entityTransformComponent.position.x = entityBody.position.x
                entityTransformComponent.position.y = entityBody.position.y

                /**
                 * Check if entity has the PlayerComponent and set transform of weapon entity to body's transform
                 */
                if (ComponentMapper.getFor(PlayerComponent::class.java).get(entity) != null) {
                    val playerComponent = ComponentMapper.getFor(PlayerComponent::class.java).get(entity)
                    val weaponTransform = ComponentMapper.getFor(TransformComponent::class.java).get(playerComponent.weapon)

                    weaponTransform.position.x = entityBody.position.x
                    weaponTransform.position.y = entityBody.position.y
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        collisionQueue.add(entity!!)
    }
}