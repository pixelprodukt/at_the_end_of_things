package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.AnimationStateComponent
import com.mygdx.ateot.components.BodyComponent
import com.mygdx.ateot.components.PlayerComponent
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.enums.Direction
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.helper.EntityFactory
import kotlin.math.cos
import kotlin.math.sin

class PlayerControlSystem(
    private val inputHandler: InputHandler,
    private val camera: OrthographicCamera,
    private val entityFactory: EntityFactory
) :
    IteratingSystem(
        Family.all(
            PlayerComponent::class.java, BodyComponent::class.java,
            AnimationStateComponent::class.java, TransformComponent::class.java
        ).get()
    ) {

    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperBodyComponent = ComponentMapper.getFor(BodyComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val playerComponent = mapperPlayerComponent.get(entity)
        val bodyComponent = mapperBodyComponent.get(entity)
        val animationStateComponent = mapperAnimationStateComponent.get(entity)
        val transformComponent = mapperTransformComponent.get(entity)

        val unprojectedMouseCoords = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val mouseX = unprojectedMouseCoords.x
        val mouseY = unprojectedMouseCoords.y

        val weaponStateComponent = playerComponent.weapon?.getComponent(AnimationStateComponent::class.java)
        val weaponTransformComponent = playerComponent.weapon?.getComponent(TransformComponent::class.java)

        if (mouseX > transformComponent.position.x && mouseY < transformComponent.position.y) {
            playerComponent.direction = Direction.DOWN_RIGHT
        }
        if (mouseX > transformComponent.position.x && mouseY > transformComponent.position.y) {
            playerComponent.direction = Direction.UP_RIGHT
        }
        if (mouseX < transformComponent.position.x && mouseY < transformComponent.position.y) {
            playerComponent.direction = Direction.DOWN_LEFT
        }
        if (mouseX < transformComponent.position.x && mouseY > transformComponent.position.y) {
            playerComponent.direction = Direction.UP_LEFT
        }

        if (mouseX < transformComponent.position.x) {
            weaponStateComponent?.state = AnimationStateComponent.WEAPON_ORIENTATION_LEFT
            weaponTransformComponent?.offset?.x = 6.0f //4.0f
            weaponTransformComponent?.offset?.y = 2.0f //2.0f
            weaponTransformComponent?.originOffset?.x = -5.0f
            weaponTransformComponent?.originOffset?.y = -0.0f
        } else {
            weaponStateComponent?.state = AnimationStateComponent.WEAPON_ORIENTATION_RIGHT
            weaponTransformComponent?.offset?.x = 4.0f //4.0f
            weaponTransformComponent?.offset?.y = 2.0f //2.0f
            weaponTransformComponent?.originOffset?.x = -5.0f
            weaponTransformComponent?.originOffset?.y = -0.0f
        }

        bodyComponent.body.velocity.set(0.0f, 0.0f)

        if (inputHandler.isUpPressed) bodyComponent.body.velocity.y = 1.0f
        if (inputHandler.isDownPressed) bodyComponent.body.velocity.y = -1.0f
        if (inputHandler.isLeftPressed) bodyComponent.body.velocity.x = -1.0f
        if (inputHandler.isRightPressed) bodyComponent.body.velocity.x = 1.0f

        playerComponent.isMoving = bodyComponent.body.velocity.x != 0f || bodyComponent.body.velocity.y != 0f

        if (playerComponent.isMoving) {
            if (playerComponent.direction == Direction.DOWN_RIGHT) {
                animationStateComponent.state = AnimationStateComponent.MOVE_DOWN_RIGHT
            }
            if (playerComponent.direction == Direction.UP_RIGHT) {
                animationStateComponent.state = AnimationStateComponent.MOVE_UP_RIGHT
            }
            if (playerComponent.direction == Direction.DOWN_LEFT) {
                animationStateComponent.state = AnimationStateComponent.MOVE_DOWN_LEFT
            }
            if (playerComponent.direction == Direction.UP_LEFT) {
                animationStateComponent.state = AnimationStateComponent.MOVE_UP_LEFT
            }
        } else {
            if (playerComponent.direction == Direction.DOWN_RIGHT) {
                animationStateComponent.state = AnimationStateComponent.IDLE_DOWN_RIGHT
            }
            if (playerComponent.direction == Direction.UP_RIGHT) {
                animationStateComponent.state = AnimationStateComponent.IDLE_UP_RIGHT
            }
            if (playerComponent.direction == Direction.DOWN_LEFT) {
                animationStateComponent.state = AnimationStateComponent.IDLE_DOWN_LEFT
            }
            if (playerComponent.direction == Direction.UP_LEFT) {
                animationStateComponent.state = AnimationStateComponent.IDLE_UP_LEFT
            }
        }

        bodyComponent.body.velocity.nor()
        bodyComponent.body.position.x += bodyComponent.body.velocity.x * 1.2f // speed
        bodyComponent.body.position.y += bodyComponent.body.velocity.y * 1.2f // speed

        transformComponent.position.x = bodyComponent.body.position.x
        transformComponent.position.y = bodyComponent.body.position.y

        val weaponTransform = playerComponent.weapon?.getComponent(TransformComponent::class.java)
        weaponTransform?.position?.x = bodyComponent.body.position.x
        weaponTransform?.position?.y = bodyComponent.body.position.y

        if (playerComponent.direction == Direction.UP_LEFT || playerComponent.direction == Direction.UP_RIGHT) {
            weaponTransform?.position?.z = -0.1f
        } else {
            weaponTransform?.position?.z = 0.1f
        }


        if (inputHandler.leftMousePressed) {
            inputHandler.leftMousePressed = false
            val spawn = Vector3(weaponTransform?.position)
            val target = Vector3(mouseX, mouseY, 0.0f)

            // Get rid of magic numbers. These here a basically distance of spawnpoint of the bullet to the weapons position
            // and an offset on the y axis for the position of the barrel of the weapon image
            // link to source for calculation of orbit: 
            // https://gamedev.stackexchange.com/questions/100802/in-libgdx-how-might-i-make-an-object-orbit-around-a-position
            val radians = Math.toRadians(weaponTransform?.rotation!!.toDouble())
            val x = ((cos(radians) * 7) + weaponTransform.position.x).toFloat()
            val y = ((sin(radians) * 7) + (weaponTransform.position.y + 1.5)).toFloat()
            val spanwPointAroundWeapon = Vector3(x, y, 0.0f)

            // Refactor: Naming is shit, I got confused myself for what the parameters are for
            entityFactory.addEntityToEngine(entityFactory.createBullet(weaponTransform!!, spawn!!, target, spanwPointAroundWeapon))
            //entityFactory.addEntityToEngine(entityFactory.createBullet(weaponTransform!!, spawn!!, target))
        }
    }
}