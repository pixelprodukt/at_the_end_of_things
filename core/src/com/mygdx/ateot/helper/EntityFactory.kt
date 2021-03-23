package com.mygdx.ateot.helper

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.constants.BulletConfig
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.data.WeaponConfigData
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.enums.ExplosionType

class EntityFactory(private val engine: PooledEngine, context: GameContext) {

    //private val packageName = "com.mygdx.ateot.components."
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
        /*val clazz = Class.forName("${packageName}TransformComponent").newInstance().javaClass as Class<Component>
        Gdx.app.log("EntityBuilder", "clazz: ${clazz}")
        Gdx.app.log("EntityBuilder", "TransformComponent: ${TransformComponent::class.java}")
        val transformComponent = engine.createComponent(clazz)*/
        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val playerComponent = engine.createComponent(PlayerComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.z = 1.0f

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

        entity.add(transformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)
        entity.add(playerComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }

    fun createWeapon(config: WeaponConfigData): Entity {
        val entity = engine.createEntity()
        val transform = engine.createComponent(TransformComponent::class.java)
        val animation = engine.createComponent(AnimationComponent::class.java)
        val animationState = engine.createComponent(AnimationStateComponent::class.java)
        val texture = engine.createComponent(TextureComponent::class.java)
        val weapon = engine.createComponent(WeaponComponent::class.java)

        weapon.bulletType = config.bulletType
        weapon.fireRate = config.fireRate

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_RIGHT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(config.assetPath), 0, 0, 16, 1, 0.2f)

        animation.animations[AnimationStateComponent.WEAPON_ORIENTATION_LEFT] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(config.assetPath), 16, 0, 16, 1, 0.2f)

        animationState.state = AnimationStateComponent.WEAPON_ORIENTATION_RIGHT
        animationState.isLooping = true

        transform.position.z = 0.2f

        weapon.muzzle = createMuzzleFlash(config.assetPath)

        engine.addEntity(weapon.muzzle)

        entity.add(transform)
        entity.add(animation)
        entity.add(animationState)
        entity.add(texture)
        entity.add(weapon)

        return entity
    }

    private fun createMuzzleFlash(assetPath: String): Entity {

        val entity = engine.createEntity()

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.z = 2.0f
        //transformComponent.isHidden = true

        animationComponent.animations[AnimationStateComponent.WEAPON_MUZZLE] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(assetPath), 0, 16, 16, 4, 0.05f)

        animationStateComponent.state = AnimationStateComponent.WEAPON_MUZZLE
        animationStateComponent.time = 55f

        entity.add(transformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }

    fun createBullet(rotation: Float, spawnCenter: Vector3, target: Vector3, spawnOnWeapon: Vector3, bulletType: BulletType): Entity {

        val entity = engine.createEntity()

        val bulletValues = BulletConfig.valuesFor[bulletType]!!

        val bulletComponent = engine.createComponent(BulletComponent::class.java)
        bulletComponent.damage = bulletValues.damage
        bulletComponent.spawn = spawnCenter
        bulletComponent.target = target
        bulletComponent.timeAlive = 0.0f
        bulletComponent.maxLifetime = bulletValues.maxLifetime
        bulletComponent.speed = bulletValues.speed
        bulletComponent.type = bulletType
        bulletComponent.explosionType = bulletValues.explosionType

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        transformComponent.position.set(spawnOnWeapon)
        transformComponent.rotation = rotation

        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        bodyComponent.body.isSensor = true
        bodyComponent.body.size.set(4.0f, 4.0f)
        bodyComponent.body.offset.set(-2.0f, -2.0f)
        bodyComponent.body.position.set(spawnOnWeapon.x, spawnOnWeapon.y)

        val weaponAsset = when (bulletType) {
            BulletType.NONE -> null
            BulletType.RIFLE_BULLET -> Assets.WEAPON_RIFLE
            BulletType.ROCKET -> Assets.WEAPON_ROCKETLAUNCHER
        }

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val texture: Texture = assetHandler.assets.get(weaponAsset)
        textureComponent.region = TextureRegion(texture, 2 * 16, 0, 16, 16)

        entity.add(bulletComponent)
        entity.add(transformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)

        return entity
    }

    fun createExplosion(data: CreateExplosionEventData): Entity {

        val entity = engine.createEntity()

        val explosionComponent = engine.createComponent(ExplosionComponent::class.java)
        val transformComponent = engine.createComponent(TransformComponent::class.java)
        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.set(data.destroyedAtVector)
        transformComponent.rotation = 0.0f

        val explosionAsset = when (data.explosionType) {
            ExplosionType.BULLET -> Assets.BULLET_EXPLOSION
            ExplosionType.ROCKET -> Assets.ROCKET_EXPLOSION
            ExplosionType.BARREL -> Assets.BARREL_EXPLOSION
        }

        val framesize = when (data.explosionType) {
            ExplosionType.BULLET -> 16
            ExplosionType.ROCKET -> 32
            ExplosionType.BARREL -> 64
        }

        animationComponent.animations[AnimationStateComponent.EXPLOSION] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(explosionAsset), 0, 0, framesize, 5, 0.05f)

        animationStateComponent.state = AnimationStateComponent.EXPLOSION

        entity.add(explosionComponent)
        entity.add(transformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }
}