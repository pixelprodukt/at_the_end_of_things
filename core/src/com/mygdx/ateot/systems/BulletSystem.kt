package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.events.GameEventListener
import com.mygdx.ateot.events.WeaponFireEvent
import com.mygdx.ateot.helper.EntityFactory
import com.mygdx.ateot.helper.GameContext
import kotlin.math.cos
import kotlin.math.sin

class BulletSystem(
    context: GameContext,
    private val entityFactory: EntityFactory,
    private val camera: OrthographicCamera
) :
    IteratingSystem(Family.all(BulletComponent::class.java, TransformComponent::class.java, DamageBodyComponent::class.java).get()) {

    private val eventHandler = context.eventHandler
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperDamageBodyComponent = ComponentMapper.getFor(DamageBodyComponent::class.java)
    private val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)
    private val mapperWeaponComponent = ComponentMapper.getFor(WeaponComponent::class.java)

    init {
        eventHandler.addListener(object : GameEventListener<WeaponFireEvent> {
            override fun handle(gameEvent: WeaponFireEvent) {

                val weaponEntity = gameEvent.data
                val weaponComponent = mapperWeaponComponent.get(weaponEntity)
                val weaponTransformComponent = mapperTransformComponent.get(weaponEntity)

                val unprojectedMouseCoords = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

                val mouseX = unprojectedMouseCoords.x
                val mouseY = unprojectedMouseCoords.y

                val spawn = Vector3(weaponTransformComponent.position)
                val target = Vector3(mouseX, mouseY, 0.0f)

                // Get rid of magic numbers. These here a basically distance of spawnpoint of the bullet to the weapons position
                // and an offset on the y axis for the position of the barrel of the weapon image
                // link to source for calculation of orbit:
                // https://gamedev.stackexchange.com/questions/100802/in-libgdx-how-might-i-make-an-object-orbit-around-a-position
                val radians = Math.toRadians(weaponTransformComponent.rotation.toDouble())
                val distanceToCenter = 16

                val x =
                    ((cos(radians) * distanceToCenter) + ((weaponTransformComponent.position.x) + weaponTransformComponent.offset.x + weaponTransformComponent.originOffset.x)).toFloat()
                val y =
                    ((sin(radians) * distanceToCenter) + ((weaponTransformComponent.position.y) + weaponTransformComponent.offset.y + weaponTransformComponent.originOffset.y)).toFloat()

                val spawnPointAroundWeapon = Vector3(x, y, 0.0f)

                // TODO Refactor: Naming is shit, I got confused myself for what the parameters are for
                entityFactory.addEntityToEngine(
                    entityFactory.createBullet(
                        weaponTransformComponent.rotation,
                        spawn,
                        target,
                        spawnPointAroundWeapon,
                        weaponComponent.bulletType
                    )
                )
            }
        })
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val bulletComponent = mapperBulletComponent.get(entity)
        val bodyComponent = mapperDamageBodyComponent.get(entity)

        val direction = Vector3().set(bulletComponent.target).sub(bulletComponent.spawn).nor()
        val velocity = Vector3().set(direction).scl(bulletComponent.speed)

        bodyComponent.body.velocity.x = velocity.x
        bodyComponent.body.velocity.y = velocity.y
        bodyComponent.body.position.x += bodyComponent.body.velocity.x
        bodyComponent.body.position.y += bodyComponent.body.velocity.y

        bulletComponent.timeAlive += deltaTime

        if (bulletComponent.timeAlive >= bulletComponent.maxLifetime) {
            entityFactory.removeFromEngine(entity!!)
        }
    }
}