package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.BulletComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.enums.GameEventType
import com.mygdx.ateot.events.BulletDestroyedEvent
import com.mygdx.ateot.events.GameEvent
import com.mygdx.ateot.events.GameEventListener
import com.mygdx.ateot.events.WeaponFireEvent
import com.mygdx.ateot.helper.EntityBuilder
import com.mygdx.ateot.helper.GameContext
import com.mygdx.ateot.helper.pointIntersectsWithBody
import kotlin.math.cos
import kotlin.math.sin

class BulletSystem(
    context: GameContext,
    private val entityBuilder: EntityBuilder,
    private val camera: OrthographicCamera
) :
    IteratingSystem(Family.all(BulletComponent::class.java).get()) {

    private val mapHandler = context.mapHandler
    private val eventHandler = context.eventHandler
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperBulletComponent = ComponentMapper.getFor(BulletComponent::class.java)

    init {
        eventHandler.addListener(object : GameEventListener<WeaponFireEvent> {
            override fun handle(gameEvent: WeaponFireEvent) {
                val weaponTransformComponent = gameEvent.data
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
                entityBuilder.addEntityToEngine(
                    entityBuilder.createBullet(
                        weaponTransformComponent,
                        spawn,
                        target,
                        spawnPointAroundWeapon
                    )
                )
            }
        })
    }

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
                eventHandler.publish(BulletDestroyedEvent(transformComponent.position))
                engine.removeEntity(entity)
            }
        }
    }

    /*override fun handle(fireEvent: WeaponFireEvent) {

        val weaponTransformComponent = fireEvent.data
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
        entityBuilder.addEntityToEngine(
            entityBuilder.createBullet(
                weaponTransformComponent,
                spawn,
                target,
                spawnPointAroundWeapon
            )
        )
    }*/
}