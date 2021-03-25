package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ateot.components.AnimationStateComponent
import com.mygdx.ateot.components.BarrelComponent
import com.mygdx.ateot.components.HitpointsComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.enums.ExplosionType
import com.mygdx.ateot.events.CreateExplosionEvent
import com.mygdx.ateot.helper.GameContext

/**
 * TODO: currently only gets barrels
 */
class HitpointsSystem(context: GameContext) : IteratingSystem(Family.all(HitpointsComponent::class.java, BarrelComponent::class.java).get()) {

    private val eventHandler = context.eventHandler

    private val mapperHitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)
    private val mapperBarrelComponent = ComponentMapper.getFor(BarrelComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        if (entity != null && mapperBarrelComponent.get(entity) != null) {

            val hitpointsComponent = mapperHitpointsComponent.get(entity)
            val animationStateComponent = mapperAnimationStateComponent.get(entity)
            val transformComponent = mapperTransformComponent.get(entity)

            if (hitpointsComponent.isDead && animationStateComponent.state != AnimationStateComponent.DEATH) {

                animationStateComponent.state = AnimationStateComponent.DEATH
                val eventData = CreateExplosionEventData(ExplosionType.BARREL, transformComponent.position)
                eventHandler.publish(CreateExplosionEvent(eventData))
            }
        }
    }
}