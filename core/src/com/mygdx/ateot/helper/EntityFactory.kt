package com.mygdx.ateot.helper

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.*
import com.mygdx.ateot.constants.Assets
import com.mygdx.ateot.constants.BulletConfig
import com.mygdx.ateot.constants.ExplosionConfig
import com.mygdx.ateot.data.CreateExplosionEventData
import com.mygdx.ateot.data.WeaponConfigData
import com.mygdx.ateot.enums.BulletType
import com.mygdx.ateot.handler.AssetHandler

class EntityFactory(private val engine: PooledEngine, private val assetHandler: AssetHandler) {

    // private val packageName = "com.mygdx.ateot.components."
    // private val assetHandler = context.assetHandler

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

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -3.0f, 0.0f)
        }

        val bodyComponent = engine.createComponent(CollisionBodyComponent::class.java).apply {
            body.position.set(100f, 60f)
            body.size.set(8f, 8f)
            body.offset.set(-4f, -7f)
            isStatic = false
            isHitable = true
        }

        val textureComponent = engine.createComponent(TextureComponent::class.java)
        val playerComponent = engine.createComponent(PlayerComponent::class.java)
        val hitpointsComponent = engine.createComponent(HitpointsComponent::class.java).apply {
            hitpoints = 20
            invincibilityTimeAfterHit = 1.0f
        }
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.z = 1.0f

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

        animationComponent.animations[AnimationStateComponent.DEATH_DOWN_LEFT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER_DEATH), 0, 0, 16, 14, 0.15f)

        animationComponent.animations[AnimationStateComponent.DEATH_DOWN_RIGHT] =
            assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.PLAYER_DEATH), 0, 16, 16, 14, 0.15f)


        animationStateComponent.state = AnimationStateComponent.MOVE_DOWN_RIGHT
        animationStateComponent.isLooping = true

        val playerTex: Texture = assetHandler.assets.get(Assets.PLAYER)
        textureComponent.region = TextureRegion(playerTex)

        entity.add(transformComponent)
        entity.add(renderingOrderTransformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)
        entity.add(playerComponent)
        entity.add(hitpointsComponent)
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

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -3.1f, 0.0f)
        }

        weapon.muzzle = createMuzzleFlash(config.assetPath)

        engine.addEntity(weapon.muzzle)

        entity.add(transform)
        entity.add(renderingOrderTransformComponent)
        entity.add(animation)
        entity.add(animationState)
        entity.add(texture)
        entity.add(weapon)

        return entity
    }

    private fun createMuzzleFlash(assetPath: String): Entity {

        val entity = engine.createEntity()

        val transformComponent = engine.createComponent(TransformComponent::class.java)

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -15.0f, 0.0f)
        }

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.z = 2.0f

        animationComponent.animations[AnimationStateComponent.WEAPON_MUZZLE] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(assetPath), 0, 16, 16, 4, 0.05f)

        animationStateComponent.state = AnimationStateComponent.WEAPON_MUZZLE
        animationStateComponent.time = 55f

        entity.add(transformComponent)
        entity.add(renderingOrderTransformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }

    fun createBullet(rotation: Float, spawnCenter: Vector3, target: Vector3, spawnOnWeapon: Vector3, bulletType: BulletType): Entity {

        val entity = engine.createEntity()

        val bulletValues = BulletConfig.valuesFor[bulletType]!!

        val bulletComponent = engine.createComponent(BulletComponent::class.java)
        bulletComponent.spawn = spawnOnWeapon
        bulletComponent.target = target
        bulletComponent.timeAlive = 0.0f
        bulletComponent.maxLifetime = bulletValues.maxLifetime
        bulletComponent.speed = bulletValues.speed
        bulletComponent.type = bulletType
        bulletComponent.explosionType = bulletValues.explosionType

        val transformComponent = engine.createComponent(TransformComponent::class.java)
        transformComponent.position.set(spawnOnWeapon)
        transformComponent.rotation = rotation

        val bodyComponent = engine.createComponent(DamageBodyComponent::class.java).apply {
            body.size.set(4.0f, 4.0f)
            body.offset.set(-2.0f, -2.0f)
            body.position.set(spawnOnWeapon.x, spawnOnWeapon.y)
            damage = bulletValues.damage
            isActive = true
        }

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val texture: Texture = assetHandler.assets.get(bulletValues.asset)
        textureComponent.region = TextureRegion(texture, 2 * 16, 0, 16, 16)

        entity.add(bulletComponent)
        entity.add(transformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)

        return entity
    }

    fun createExplosion(data: CreateExplosionEventData): Entity {

        val entity = engine.createEntity()

        val explosionValues = ExplosionConfig.valuesFor[data.explosionType]!!

        val explosionComponent = engine.createComponent(ExplosionComponent::class.java)
        val transformComponent = engine.createComponent(TransformComponent::class.java)

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -20.0f, 0.0f)
        }

        val bodyComponent = engine.createComponent(DamageBodyComponent::class.java).apply {
            body.size.set(explosionValues.bodySize)
            body.position.set(data.spawn.x, data.spawn.y)
            body.offset.set(-(explosionValues.bodySize.x / 2), -(explosionValues.bodySize.y / 2))
            damage = explosionValues.damage
        }

        var textureComponent = engine.createComponent(TextureComponent::class.java)
        val animationComponent = engine.createComponent(AnimationComponent::class.java)
        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java)

        transformComponent.position.set(data.spawn)
        transformComponent.rotation = 0.0f

        animationComponent.animations[AnimationStateComponent.EXPLOSION] = assetHandler.animationHelper.createAnimation(
            assetHandler.assets.get(explosionValues.asset), 0, 0, explosionValues.frameSize, 5, 0.05f)

        animationStateComponent.state = AnimationStateComponent.EXPLOSION

        entity.add(explosionComponent)
        entity.add(transformComponent)
        entity.add(renderingOrderTransformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        return entity
    }

    fun createExplosiveBarrel(spawn: Vector2) {

        val entity = engine.createEntity()

        val barrelComponent = engine.createComponent(BarrelComponent::class.java)

        val bodyComponent = engine.createComponent(CollisionBodyComponent::class.java).apply {
            body.size.set(16.0f, 8.0f)
            body.position.set(spawn)
            body.offset.set(-8.0f, -8.0f)
            isStatic = true
            isHitable = true
        }

        val hitpointsComponent = engine.createComponent(HitpointsComponent::class.java).apply {
            hitpoints = 10
            invincibilityTimeAfterHit = 0.2f
        }

        val transformComponent = engine.createComponent(TransformComponent::class.java)

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -1.0f, 0.0f)
        }

        val textureComponent = engine.createComponent(TextureComponent::class.java)

        val animationComponent = engine.createComponent(AnimationComponent::class.java).apply {
            animations[AnimationStateComponent.STATIC_IDLE] = assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.EXPLOSIVE_BARRELS), 0, 0, 16, 1, 0.05f)
            animations[AnimationStateComponent.DEATH] = assetHandler.animationHelper.createAnimation(
                assetHandler.assets.get(Assets.EXPLOSIVE_BARRELS), 16, 0, 16, 1, 0.05f)
        }

        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java).apply {
            isLooping = false
            state = AnimationStateComponent.STATIC_IDLE
        }

        entity.add(barrelComponent)
        entity.add(bodyComponent)
        entity.add(hitpointsComponent)
        entity.add(transformComponent)
        entity.add(renderingOrderTransformComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)

        engine.addEntity(entity)
    }

    fun createCollisionEntity(position: Vector2, size: Vector2, isWall: Boolean) {

        val entity = engine.createEntity()

        val bodyComponent = engine.createComponent(CollisionBodyComponent::class.java).apply {
            body.position.set(position)
            body.size.set(size)
            isHitable = isWall
            isStatic = true
        }

        entity.add(bodyComponent)

        engine.addEntity(entity)
    }

    fun createFleshblob(spawn: Vector2) {

        val entity = engine.createEntity()

        val transformComponent = engine.createComponent(TransformComponent::class.java)

        val renderingOrderTransformComponent = engine.createComponent(RenderingOrderTransformComponent::class.java).apply {
            offset.set(0.0f, -3.0f, 0.0f)
        }

        val bodyComponent = engine.createComponent(CollisionBodyComponent::class.java).apply {
            body.position.set(spawn)
            body.size.set(8.0f, 8.0f)
            body.offset.set(-4.0f, -2.0f)
            isStatic = false
            isHitable = true
        }

        val damageBodyComponent = engine.createComponent(DamageBodyComponent::class.java).apply {
            body.position.set(spawn)
            body.size.set(12.0f, 12.0f)
            body.offset.set(-6.0f, -4.0f)
            damage = 5
            isActive = true
        }

        val textureComponent = engine.createComponent(TextureComponent::class.java).apply {
            val tex: Texture = assetHandler.assets.get(Assets.ENEMY_FLESHBLOB)
            region = TextureRegion(tex)
        }

        val animationComponent = engine.createComponent(AnimationComponent::class.java).apply {
            animations[AnimationStateComponent.IDLE_DOWN_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 0, 16, 2, 0.5f)
            animations[AnimationStateComponent.IDLE_DOWN_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16, 16, 2, 0.5f)
            animations[AnimationStateComponent.IDLE_UP_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16 * 2, 16, 2, 0.5f)
            animations[AnimationStateComponent.IDLE_UP_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16 * 3, 16, 2, 0.5f)

            animations[AnimationStateComponent.MOVE_DOWN_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 0, 16, 2, 0.5f)
            animations[AnimationStateComponent.MOVE_DOWN_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16, 16, 2, 0.5f)
            animations[AnimationStateComponent.MOVE_UP_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16 * 2, 16, 2, 0.5f)
            animations[AnimationStateComponent.MOVE_UP_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 0, 16 * 3, 16, 2, 0.5f)

            animations[AnimationStateComponent.DEATH_DOWN_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 32, 0, 16, 3, 0.15f)
            animations[AnimationStateComponent.DEATH_DOWN_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 32, 16, 16, 3, 0.15f)
            animations[AnimationStateComponent.DEATH_UP_RIGHT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 32, 16 * 2, 16, 3, 0.15f)
            animations[AnimationStateComponent.DEATH_UP_LEFT] =
                assetHandler.animationHelper.createAnimation(
                    assetHandler.assets.get(Assets.ENEMY_FLESHBLOB), 32, 16 * 3, 16, 3, 0.15f)
        }

        val animationStateComponent = engine.createComponent(AnimationStateComponent::class.java).apply {
            state = AnimationStateComponent.IDLE_DOWN_LEFT
            isLooping = true
        }

        val enemyComponent = engine.createComponent(EnemyComponent::class.java)
        val hitpointsComponent = engine.createComponent(HitpointsComponent::class.java).apply {
            hitpoints = 15
            invincibilityTimeAfterHit = 0.3f
        }

        entity.add(transformComponent)
        entity.add(renderingOrderTransformComponent)
        entity.add(bodyComponent)
        entity.add(textureComponent)
        entity.add(animationComponent)
        entity.add(animationStateComponent)
        entity.add(enemyComponent)
        entity.add(hitpointsComponent)
        entity.add(damageBodyComponent)

        engine.addEntity(entity)
    }
}