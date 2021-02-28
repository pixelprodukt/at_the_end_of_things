package com.mygdx.ateot.helper

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.handler.AssetHandler

class EntityFactory(private val engine: Engine, private val assetHandler: AssetHandler) {

    fun addEntityToEngine(entity: Entity) {
        engine.addEntity(entity)
    }

    fun createPlayer(): Entity {
        val entity = engine.createEntity()
        val transform = engine.createComponent(TransformComponent::class.java)
        val body = engine.createComponent(BodyComponent::class.java)
        val texture = engine.createComponent(TextureComponent::class.java)
        val player = engine.createComponent(PlayerComponent::class.java)
        val animation = engine.createComponent(AnimationComponent::class.java)
        val animationState = engine.createComponent(AnimationStateComponent::class.java)

        body.body.position.x = 100f
        body.body.position.y = 60f

        body.body.size.set(8f, 8f)
        body.body.offset.set(-4f, -7f)

        animation.animations[AnimationStateComponent.IDLE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 0, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 2, 16, 1, 0.1f)
        animation.animations[AnimationStateComponent.IDLE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 3, 16, 1, 0.1f)

        animation.animations[AnimationStateComponent.MOVE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 0, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 2, 16, 4, 0.15f)
        animation.animations[AnimationStateComponent.MOVE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 3, 16, 4, 0.15f)

        animationState.state = AnimationStateComponent.MOVE_DOWN_RIGHT
        animationState.isLooping = true

        val playerTex: Texture = assetHandler.assets.get(Assets.PLAYER)
        texture.region = TextureRegion(playerTex)

        player.weapon = createWeapon()

        engine.addEntity(player.weapon)

        entity.add(transform)
        entity.add(body)
        entity.add(texture)
        entity.add(player)
        entity.add(animation)
        entity.add(animationState)

        return entity
    }

    private fun createWeapon(): Entity {
        val entity = engine.createEntity()
        val transform = engine.createComponent(TransformComponent::class.java)
        val animation = engine.createComponent(AnimationComponent::class.java)
        val animationState = engine.createComponent(AnimationStateComponent::class.java)
        val texture = engine.createComponent(TextureComponent::class.java)
        val weapon = engine.createComponent(WeaponComponent::class.java)

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_RIGHT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.WEAPON_RIFLE), 0, 0, 16, 1, 0.2f)

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_LEFT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.WEAPON_RIFLE), 16, 0, 16, 1, 0.2f)

        animationState.state = AnimationStateComponent.WEAPON_ORIENTATION_RIGHT
        animationState.isLooping = true

        transform.position.z = 0.1f

        entity.add(transform)
        entity.add(animation)
        entity.add(animationState)
        entity.add(texture)
        entity.add(weapon)

        return entity
    }

    fun createBullet(spawnTransform: TransformComponent, spawnVector: Vector3, targetVector: Vector3, spanwPointAroundWeapon: Vector3): Entity {

        val entity = engine.createEntity()

        val bulletComponent = engine.createComponent(BulletComponent::class.java)
        bulletComponent.spawn = spawnVector
        bulletComponent.target = targetVector
        // Why do I have to set time on creation? If not, timeAlive is like a global for all bullets
        bulletComponent.timeAlive = 0.0f

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        transformComponent.position.set(spanwPointAroundWeapon)
        transformComponent.rotation = spawnTransform.rotation

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val texture: Texture = assetHandler.assets.get(Assets.RIFLE_MUZZLE_BULLET)
        textureComponent.region = TextureRegion(texture, 16, 0, 16, 16)

        entity.add(bulletComponent)
        entity.add(transformComponent)
        entity.add(textureComponent)

        return entity
    }
}