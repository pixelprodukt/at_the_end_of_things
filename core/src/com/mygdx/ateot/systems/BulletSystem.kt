package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.BulletComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.handler.MapHandler
import com.mygdx.ateot.helper.pointIntersectsWithBody

class BulletSystem(private val mapHandler: MapHandler) : IteratingSystem(Family.all(BulletComponent::class.java).get()) {

    val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val transformComponent = mapperTransformComponent.get(entity)
        val bulletComponent = mapperBulletComponent.get(entity)

        val direction = Vector3().set(bulletComponent.target).sub(bulletComponent.spawn).nor()
        val velocity = Vector3().set(direction).scl(bulletComponent.speed)

        // How do I get it to work that a bullet will add the current velocity of the player?
        // So as the player you can't outrun even a slow bullet
        // Edit: The current solution is more 'realistic'? Otherwise it could have some strange
        // side effects like a later bullet outspeeding an earlier one
        transformComponent.position.x += velocity.x
        transformComponent.position.y += velocity.y

        bulletComponent.timeAlive += deltaTime

        if (bulletComponent.timeAlive >= bulletComponent.maxLifetime) {
            engine.removeEntity(entity)
        }

        // Works but needs a distinction between map walls and other not-passable stuff (like water)
        mapHandler.staticWallBodies.forEach { body ->

            if (pointIntersectsWithBody(transformComponent.position, body)) {
                engine.removeEntity(entity)
            }
        }

    }
}