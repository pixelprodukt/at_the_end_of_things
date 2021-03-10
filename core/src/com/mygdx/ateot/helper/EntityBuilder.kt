package com.mygdx.ateot.helper

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets

class EntityBuilder(private val engine: Engine, context: GameContext) {

    private val assetHandler = context.assetHandler

    fun addEntityToEngine(entity: Entity) {
        engine.addEntity(entity)
    }

    fun removeFromEngine(entity: Entity) {
        engine.removeEntity(entity)
    }

    fun createPlayer(): Entity {
        val entity = engine.createEntity()
        val transformComponent = engine.createComponent(TransformComponent::class.java)
        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val playerComponent = engine.createComponent(PlayerComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        bodyComponent.body.position.x = 100f
        bodyComponent.body.position.y = 60f

        bodyComponent.body.size.set(8f, 8f)
        bodyComponent.body.offset.set(-4f, -7f)

        animationComponent.animations[AnimationStateComponent.IDLE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 0, 16, 1, 0.1f)
        animationComponent.animations[AnimationStateComponent.IDLE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16, 16, 1, 0.1f)
        animationComponent.animations[AnimationStateComponent.IDLE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 2, 16, 1, 0.1f)
        animationComponent.animations[AnimationStateComponent.IDLE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 0, 16 * 3, 16, 1, 0.1f)

        animationComponent.animations[AnimationStateComponent.MOVE_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 0, 16, 4, 0.15f)
        animationComponent.animations[AnimationStateComponent.MOVE_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16, 16, 4, 0.15f)
        animationComponent.animations[AnimationStateComponent.MOVE_UP_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 2, 16, 4, 0.15f)
        animationComponent.animations[AnimationStateComponent.MOVE_UP_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER), 16, 16 * 3, 16, 4, 0.15f)

        animationStateComponent.state = AnimationStateComponent.MOVE_DOWN_RIGHT
        animationStateComponent.isLooping = true

        val playerTex: Texture = assetHandler.assets.get(Assets.PLAYER)
        textureComponent.region = TextureRegion(playerTex)

        playerComponent.weapon = createWeapon()

        engine.addEntity(playerComponent.weapon)

        entity.add(transformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)
        entity.add(playerComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

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

        weapon.muzzle = createMuzzleFlash()

        engine.addEntity(weapon.muzzle)

        entity.add(transform)
        entity.add(animation)
        entity.add(animationState)
        entity.add(texture)
        entity.add(weapon)

        return entity
    }

    fun createBullet(spawnTransform: TransformComponent, spawnVector: Vector3, targetVector: Vector3, spawnPointAroundWeapon: Vector3): Entity {

        val entity = engine.createEntity()

        val bulletComponent = engine.createComponent(BulletComponent::class.java)
        bulletComponent.spawn = spawnVector
        bulletComponent.target = targetVector
        // Why do I have to set time on creation? If not, timeAlive is like a global for all bullets
        bulletComponent.timeAlive = 0.0f

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        transformComponent.position.set(spawnPointAroundWeapon)
        transformComponent.rotation = spawnTransform.rotation

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val texture: Texture = assetHandler.assets.get(Assets.RIFLE_MUZZLE_BULLET)
        textureComponent.region = TextureRegion(texture, 4 * 16, 0, 16, 16)

        entity.add(bulletComponent)
        entity.add(transformComponent)
        entity.add(textureComponent)

        return entity
    }

    fun createMuzzleFlash(): Entity {

        val entity = engine.createEntity()

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.z = 111.5f
        transformComponent.isHidden = true

        animationComponent.animations[AnimationStateComponent.WEAPON_MUZZLE] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.RIFLE_MUZZLE_BULLET), 0, 0, 16, 4, 0.05f)

        animationStateComponent.state = AnimationStateComponent.WEAPON_MUZZLE
        animationStateComponent.time = 55f

        entity.add(transformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }

    fun createExplosion(data: Vector3): Entity {

        val entity = engine.createEntity()

        val explosionComponent = engine.createComponent(ExplosionComponent::class.java)
        val transformComponent = engine.createComponent(TransformComponent::class.java)
        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.set(data)

        animationComponent.animations[AnimationStateComponent.WEAPON_EXPLOSION] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(Assets.RIFLE_EXPLOSION), 0, 0, 16, 5, 0.05f)

        animationStateComponent.state = AnimationStateComponent.WEAPON_EXPLOSION

        entity.add(explosionComponent)
        entity.add(transformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }
}