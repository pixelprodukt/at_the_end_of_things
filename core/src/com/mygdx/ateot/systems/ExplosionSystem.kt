package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ateot.components.AnimationComponent
import com.mygdx.ateot.components.AnimationStateComponent
import com.mygdx.ateot.components.ExplosionComponent
import com.mygdx.ateot.events.BulletDestroyedEvent
import com.mygdx.ateot.events.GameEventListener
import com.mygdx.ateot.helper.EntityFactory
import com.mygdx.ateot.helper.GameContext

class ExplosionSystem(
    context: GameContext, private val entityFactory: EntityFactory
    ) :
    IteratingSystem(Family.all(ExplosionComponent::class.java).get()) {

    private val eventHandler = context.eventHandler
    private val mapperAnimationComponent = ComponentMapper.getFor(AnimationComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)

    init {
        eventHandler.addListener(object : GameEventListener<BulletDestroyedEvent> {
            override fun handle(gameEvent: BulletDestroyedEvent) {
                entityFactory.addEntityToEngine(entityFactory.createExplosion(gameEvent.data))
            }
        })
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val animationComponent = mapperAnimationComponent.get(entity)
        val animationStateComponent = mapperAnimationStateComponent.get(entity)

        if (animationComponent.animations[AnimationStateComponent.WEAPON_EXPLOSION]?.isAnimationFinished(animationStateComponent.time) == true) {
            animationStateComponent.time = 0.0f
            entityFactory.removeFromEngine(entity!!)
        }
    }
}