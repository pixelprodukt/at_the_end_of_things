package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.enums.Direction
import com.mygdx.ateot.events.WeaponFireEvent
import com.mygdx.ateot.helper.GameContext

class PlayerControlSystem(
    private val context: GameContext,
    private val camera: OrthographicCamera,
) :
    IteratingSystem(
        Family.all(
            PlayerComponent::class.java, CollisionBodyComponent::class.java,
            AnimationStateComponent::class.java, TransformComponent::class.java
        ).get()
    ) {

    private val inputHandler = context.inputHandler
    private val eventHandler = context.eventHandler
    private val assetHandler = context.assetHandler
    private val mapperPlayerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    private val mapperBodyComponent = ComponentMapper.getFor(CollisionBodyComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperRenderingOrderTransformComponent = ComponentMapper.getFor(RenderingOrderTransformComponent::class.java)
    private val mapperHitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java)

    private var fireDelay = 0.0f
    private var stepSoundInterval = 0.0f

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val playerComponent = mapperPlayerComponent.get(entity)
        val bodyComponent = mapperBodyComponent.get(entity)
        val animationStateComponent = mapperAnimationStateComponent.get(entity)
        val transformComponent = mapperTransformComponent.get(entity)
        val hitpointsComponent = mapperHitpointsComponent.get(entity)

        if (hitpointsComponent.isAlive) {

            val unprojectedMouseCoords = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            val mouseX = unprojectedMouseCoords.x
            val mouseY = unprojectedMouseCoords.y

            val weaponStateComponent = mapperAnimationStateComponent.get(playerComponent.weapon)
            val weaponTransformComponent = mapperTransformComponent.get(playerComponent.weapon)

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

                playStepSound(deltaTime)

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

            val weaponRenderingOrderTransformComponent = mapperRenderingOrderTransformComponent.get(playerComponent.weapon)

            if (playerComponent.direction == Direction.UP_LEFT || playerComponent.direction == Direction.UP_RIGHT) {
                weaponRenderingOrderTransformComponent.offset.set(0.0f, -2.9f, 0.0f)
            } else {
                weaponRenderingOrderTransformComponent.offset.set(0.0f, -3.1f, 0.0f)
            }

            fireDelay -= Gdx.graphics.deltaTime

            if (inputHandler.leftMousePressed) {

                val weaponComponent = playerComponent.weapon?.getComponent(WeaponComponent::class.java)!!
                val muzzleTransformComponent = mapperTransformComponent.get(weaponComponent.muzzle)!!
                val muzzleAnimationStateComponent = mapperAnimationStateComponent.get(weaponComponent.muzzle)!!

                if (fireDelay <= 0.0f) {
                    muzzleAnimationStateComponent.time = 0.0f
                    muzzleTransformComponent.isHidden = false
                    eventHandler.publish(WeaponFireEvent(playerComponent.weapon!!))
                    fireDelay = weaponComponent.fireRate

                    assetHandler.rifleshotSound.play(context.masterVolume)
                }
            }
        }
    }

    private fun playStepSound(deltaTime: Float) {

        if (stepSoundInterval == 0.0f) {
            assetHandler.getRandomStepSound().play(context.masterVolume)
        }
        if (stepSoundInterval < 0.35f) {
            stepSoundInterval += deltaTime
        } else {
            stepSoundInterval = 0.0f
        }
    }
}