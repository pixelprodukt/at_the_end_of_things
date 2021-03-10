package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.mygdx.ateot.components.AnimationComponent
import com.mygdx.ateot.components.AnimationStateComponent
import com.mygdx.ateot.components.TextureComponent

class AnimationSystem : IteratingSystem(
    Family.all(TextureComponent::class.java, AnimationComponent::class.java, AnimationStateComponent::class.java).get()
) {

    private val mapperTextureComponent = ComponentMapper.getFor(TextureComponent::class.java)
    private val mapperAnimationComponent = ComponentMapper.getFor(AnimationComponent::class.java)
    private val mapperAnimationStateComponent = ComponentMapper.getFor(AnimationStateComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val animationComponent = mapperAnimationComponent.get(entity)
        val animationStateComponent = mapperAnimationStateComponent.get(entity)

        if (animationComponent.animations[animationStateComponent.state] !== null) {

            val textureComponent = mapperTextureComponent.get(entity)

            textureComponent.region =
                animationComponent.animations[animationStateComponent.state]?.getKeyFrame(
                    animationStateComponent.time,
                    animationStateComponent.isLooping
                )
        }

        animationStateComponent.time += deltaTime
    }
}