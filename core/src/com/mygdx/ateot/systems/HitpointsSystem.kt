package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.mygdx.ateot.components.*
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.enums.ExplosionType
import com.mygdx.ateot.events.CreateExplosionEvent
import com.mygdx.ateot.helper.GameContext

/**
 * TODO: currently only gets barrels
 */
class HitpointsSystem(context: GameContext) : IteratingSystem(Family.all(HitpointsComponent::class.java).get()) {

    private val eventHandler = context.eventHandler

    private val mapperHitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)
    private val mapperBarrelComponent = ComponentMapper.getFor(BarrelComponent::class.java)
    private val mapperEnemyComponent = ComponentMapper.getFor(EnemyComponent::class.java)
    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperDamageBodyComponent = ComponentMapper.getFor(DamageBodyComponent::class.java)

    private val hitpointsQueue = mutableListOf<Entity>()

    override fun update(deltaTime: Float) {

        hitpointsQueue.forEach { entity ->

            val hitpointsComponent = mapperHitpointsComponent.get(entity)

            if (hitpointsComponent.isInvincible) {

                hitpointsComponent.timeSinceLastHit += deltaTime

                if (hitpointsComponent.timeSinceLastHit >= hitpointsComponent.invincibilityTimeAfterHit) {
                    hitpointsComponent.isInvincible = false
                    hitpointsComponent.timeSinceLastHit = 0.0f
                }
            }
        }

        hitpointsQueue.clear()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        /**
         * barrels
         */
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

        /**
         * enemies
         */
        if (entity != null && mapperEnemyComponent.get(entity) != null) {

            val hitpointsComponent = mapperHitpointsComponent.get(entity)
            val animationStateComponent = mapperAnimationStateComponent.get(entity)

            if (hitpointsComponent.isDead && (
                        animationStateComponent.state != AnimationStateComponent.DEATH_UP_RIGHT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_UP_LEFT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_DOWN_RIGHT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_DOWN_LEFT)
            ) {

                when (animationStateComponent.state) {
                    AnimationStateComponent.IDLE_DOWN_LEFT, AnimationStateComponent.MOVE_DOWN_LEFT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_LEFT
                    AnimationStateComponent.IDLE_DOWN_RIGHT, AnimationStateComponent.MOVE_DOWN_RIGHT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_RIGHT
                    AnimationStateComponent.IDLE_UP_LEFT, AnimationStateComponent.MOVE_UP_LEFT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_UP_LEFT
                    AnimationStateComponent.IDLE_UP_RIGHT, AnimationStateComponent.MOVE_UP_RIGHT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_UP_RIGHT
                }

                animationStateComponent.isLooping = false

                val damageBodyComponent = mapperDamageBodyComponent.get(entity)

                if (damageBodyComponent != null) {
                    damageBodyComponent.isActive = false
                }
            }
        }

        /**
         * player death
         */
        if (entity != null && mapperPlayerComponent.get(entity) != null) {

            val hitpointsComponent = mapperHitpointsComponent.get(entity)
            val animationStateComponent = mapperAnimationStateComponent.get(entity)

            if (hitpointsComponent.isDead && (
                        animationStateComponent.state != AnimationStateComponent.DEATH_UP_RIGHT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_UP_LEFT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_DOWN_RIGHT ||
                                animationStateComponent.state != AnimationStateComponent.DEATH_DOWN_LEFT)
            ) {

                val weaponTransform = mapperTransformComponent.get(mapperPlayerComponent.get(entity).weapon)
                weaponTransform.isHidden = true
                /**
                 * currently, there are only "DOWN" animations for the players death. maybe I'll leave it that way
                 */
                when (animationStateComponent.state) {
                    AnimationStateComponent.IDLE_DOWN_LEFT, AnimationStateComponent.MOVE_DOWN_LEFT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_LEFT
                    AnimationStateComponent.IDLE_DOWN_RIGHT, AnimationStateComponent.MOVE_DOWN_RIGHT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_RIGHT
                    AnimationStateComponent.IDLE_UP_LEFT, AnimationStateComponent.MOVE_UP_LEFT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_LEFT
                    AnimationStateComponent.IDLE_UP_RIGHT, AnimationStateComponent.MOVE_UP_RIGHT ->
                        animationStateComponent.state = AnimationStateComponent.DEATH_DOWN_RIGHT
                }

                animationStateComponent.isLooping = false
            }
        }

        hitpointsQueue.add(entity!!)
    }
}