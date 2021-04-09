package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.events.CreateExplosionEvent
import com.mygdx.ateot.helper.*

class CollisionSystem(context: GameContext, private val entityFactory: EntityFactory) :
    IteratingSystem(Family.all(CollisionBodyComponent::class.java).get()) {

    private val eventHandler = context.eventHandler
    private val engine = context.engine

    private val staticCollisionQueue = mutableListOf<Entity>()
    private val dynamicCollisionQueue = mutableListOf<Entity>()
    private val hitableCollisionQueue = mutableListOf<Entity>()

    private val mapperCollisionBodyComponent = ComponentMapper.getFor(CollisionBodyComponent::class.java)
    private val mapperDamageBodyComponent = ComponentMapper.getFor(DamageBodyComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)
    private val mapperHitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        /**
         * collision for movable objects with static bodies (walls, barrels, etc.)
         */
        dynamicCollisionQueue.forEach { dynamicCollisionEntity ->

            val dynamicBody = mapperCollisionBodyComponent.get(dynamicCollisionEntity).body

            staticCollisionQueue.forEach { staticCollisionEntity ->

                val staticBody = mapperCollisionBodyComponent.get(staticCollisionEntity).body

                resolveCollision(dynamicBody, staticBody)

                /**
                 * Check if entity has the PlayerComponent and set transform of weapon entity to body's transform
                 * This is here to avoid rendering bugs of the weapon, where the weapon moves a bit forward when
                 * the player is moving against a wall
                 */
                if (mapperPlayerComponent.get(dynamicCollisionEntity) != null) {
                    val playerComponent = mapperPlayerComponent.get(dynamicCollisionEntity)

                    if (playerComponent.weapon != null) {
                        val weaponTransform = mapperTransformComponent.get(playerComponent.weapon)

                        weaponTransform.position.x = dynamicBody.position.x
                        weaponTransform.position.y = dynamicBody.position.y
                    }
                }
            }
        }

        /**
         * bulletcollisions are handled here
         */
        val bulletFamily = Family.all(BulletComponent::class.java).get()
        val bulletEntities = engine.getEntitiesFor(bulletFamily)

        bulletEntities.forEach { bulletEntity ->

            hitableCollisionQueue.forEach { hitableCollisionEntity ->

                val hitableBody = mapperCollisionBodyComponent.get(hitableCollisionEntity).body
                val bulletBody = mapperDamageBodyComponent.get(bulletEntity).body
                val bulletComponent = mapperBulletComponent.get(bulletEntity)
                val bulletTransformComponent = mapperTransformComponent.get(bulletEntity)

                if (intersect(bulletBody, hitableBody)) {
                    val eventData =
                        CreateExplosionEventData(bulletComponent.explosionType, bulletTransformComponent.position)
                    eventHandler.publish(CreateExplosionEvent(eventData))
                    entityFactory.removeFromEngine(bulletEntity)
                }
            }
        }

        /**
         * damage handling. put it here for the sake of testing, not sure if it should have a seperate system
         */
        val damageBodyFamily = Family.all(DamageBodyComponent::class.java).get()
        val damageBodyEntities = engine.getEntitiesFor(damageBodyFamily)
        val hitpointsCollisionBodyFamily = Family.all(HitpointsComponent::class.java, CollisionBodyComponent::class.java).get()
        val hitpointsCollisionBodyEntities = engine.getEntitiesFor(hitpointsCollisionBodyFamily)

        damageBodyEntities.forEach { damageBodyEntity ->

            val damageBodyComponent = mapperDamageBodyComponent.get(damageBodyEntity)

            hitpointsCollisionBodyEntities.forEach { hitpointsCollisionBodyEntity ->

                val collisionBodyComponent = mapperCollisionBodyComponent.get(hitpointsCollisionBodyEntity)
                val damageEntityCollisionBodyComponent = mapperCollisionBodyComponent.get(damageBodyEntity)

                if (damageEntityCollisionBodyComponent !== collisionBodyComponent) {
                    if (intersect(damageBodyComponent.body, collisionBodyComponent.body) && damageBodyComponent.isActive) {

                        val hitpointsComponent = mapperHitpointsComponent.get(hitpointsCollisionBodyEntity)

                        if (!hitpointsComponent.isInvincible && hitpointsComponent.isAlive && damageBodyComponent.damage > 0) {

                            hitpointsComponent.hitpoints -= damageBodyComponent.damage

                            if (hitpointsComponent.isAlive) {
                                hitpointsComponent.isInvincible = true
                            }

                            /**
                             * for testing purposes here. trying out knockback
                             * edit: guess knockback is canceled
                             */
                            /*val damageBodyPosition = mapperTransformComponent.get(damageBodyEntity).position
                            val hitpointsBodyPosition = mapperTransformComponent.get(hitpointsCollisionBodyEntity).position
                            val hitpointsBodyComponent = mapperCollisionBodyComponent.get(hitpointsCollisionBodyEntity)

                            val direction = Vector2().set(hitpointsBodyPosition.x, hitpointsBodyPosition.y).sub(damageBodyPosition.x, damageBodyPosition.y).nor()
                            val velocity = Vector2().set(direction).scl(11.0f)

                            hitpointsBodyComponent.body.velocity.x = velocity.x
                            hitpointsBodyComponent.body.velocity.y = velocity.y
                            hitpointsBodyComponent.body.position.x += hitpointsBodyComponent.body.velocity.x
                            hitpointsBodyComponent.body.position.y += hitpointsBodyComponent.body.velocity.y*/
                        }
                    }
                }
            }
        }

        staticCollisionQueue.clear()
        dynamicCollisionQueue.clear()
        hitableCollisionQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        if (entity != null) {

            val bodyComponent = mapperCollisionBodyComponent.get(entity)

            if (!bodyComponent.isStatic) {
                dynamicCollisionQueue.add(entity)
            }

            if (bodyComponent.isStatic) {
                staticCollisionQueue.add(entity)
            }

            if (bodyComponent.isHitable) {
                hitableCollisionQueue.add(entity)
            }
        }
    }
}