package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.mygdx.ateot.components.CollisionBodyComponent
import com.mygdx.ateot.components.DamageBodyComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.helper.GameContext

class SyncTransformAndBodiesSystem(context: GameContext) : EntitySystem() {

    private val engine = context.engine

    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperCollisionBodyComponent = ComponentMapper.getFor(CollisionBodyComponent::class.java)
    private val mapperDamageBodyComponent = ComponentMapper.getFor(DamageBodyComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val collisionBodiesWithTransformFamily = Family.all(TransformComponent::class.java, CollisionBodyComponent::class.java).get()
        val collisionBodiesWithTransformEntities = engine.getEntitiesFor(collisionBodiesWithTransformFamily)

        collisionBodiesWithTransformEntities.forEach { entity ->
            val transformComponent = mapperTransformComponent.get(entity)
            val collisionBodyComponent = mapperCollisionBodyComponent.get(entity)

            transformComponent.position.x = collisionBodyComponent.body.position.x
            transformComponent.position.y = collisionBodyComponent.body.position.y
        }

        val damageBodiesWithTransformFamily = Family.all(TransformComponent::class.java, DamageBodyComponent::class.java).get()
        val damageBodiesWithTransformEntities = engine.getEntitiesFor(damageBodiesWithTransformFamily)

        damageBodiesWithTransformEntities.forEach { entity ->
            val transformComponent = mapperTransformComponent.get(entity)
            val damageBodyComponent = mapperDamageBodyComponent.get(entity)
            val collisionBodyComponent = mapperCollisionBodyComponent.get(entity)

            if (collisionBodyComponent == null) {
                transformComponent.position.x = damageBodyComponent.body.position.x
                transformComponent.position.y = damageBodyComponent.body.position.y
            } else {
                damageBodyComponent.body.position.x = transformComponent.position.x
                damageBodyComponent.body.position.y = transformComponent.position.y
            }
        }
    }
}