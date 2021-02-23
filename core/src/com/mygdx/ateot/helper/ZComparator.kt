package com.mygdx.ateot.helper

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.mygdx.ateot.components.TransformComponent

class ZComparator : Comparator<Entity> {

    private val mapperTransformComponent: ComponentMapper<TransformComponent> =
        ComponentMapper.getFor(TransformComponent::class.java)

    override fun compare(entityA: Entity?, entityB: Entity?): Int {

        val az = mapperTransformComponent.get(entityA).position.z
        val bz = mapperTransformComponent.get(entityB).position.z

        var result = 0

        if (az > bz) {
            result = 1
        } else if (az < bz) {
            result = -1
        }
        return result
    }

}