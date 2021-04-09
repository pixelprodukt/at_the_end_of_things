package com.mygdx.ateot.helper

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.mygdx.ateot.components.RenderingOrderTransformComponent
import com.mygdx.ateot.components.TransformComponent

class RenderingOrderTransformComparator : Comparator<Entity> {

    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperRenderingOrderTransformComponent =
        ComponentMapper.getFor(RenderingOrderTransformComponent::class.java)

    override fun compare(entityA: Entity?, entityB: Entity?): Int {

        val ay = getTransformCoordinateForRenderOrder(entityA!!)
        val by = getTransformCoordinateForRenderOrder(entityB!!)

        var result = 0

        if (ay > by) {
            result = -1
        } else if (ay < by) {
            result = 1
        }

        return result
    }

    private fun getTransformCoordinateForRenderOrder(entity: Entity): Float {
        return if (mapperRenderingOrderTransformComponent.get(entity) != null) {
            mapperRenderingOrderTransformComponent.get(entity).position.y +
                    mapperRenderingOrderTransformComponent.get(entity).offset.y
        } else {
            mapperTransformComponent.get(entity).position.y
        }
    }
}