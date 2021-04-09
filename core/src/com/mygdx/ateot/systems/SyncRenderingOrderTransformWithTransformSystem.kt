package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ateot.components.RenderingOrderTransformComponent
import com.mygdx.ateot.components.TransformComponent

class SyncRenderingOrderTransformWithTransformSystem :
    IteratingSystem(Family.all(TransformComponent::class.java, RenderingOrderTransformComponent::class.java).get()) {

    val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    val mapperRenderingOrderTransformComponent = ComponentMapper.getFor(RenderingOrderTransformComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val transformComponent = mapperTransformComponent.get(entity)
        val renderingOrderTransformComponent = mapperRenderingOrderTransformComponent.get(entity)

        renderingOrderTransformComponent.position.x = transformComponent.position.x + renderingOrderTransformComponent.offset.x
        renderingOrderTransformComponent.position.y = transformComponent.position.y + renderingOrderTransformComponent.offset.y
    }
}