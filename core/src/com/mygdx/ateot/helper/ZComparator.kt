package com.mygdx.ateot.helper

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.mygdx.ateot.components.TransformComponent

class ZComparator : Comparator<Entity> {

    private val mapperTransformComponent: ComponentMapper<TransformComponent> =
        ComponentMapper.getFor(TransformComponent::class.java)

    override fun compare(entityA: Entity?, entityB: Entity?): Int {

        val ay = mapperTransformComponent.get(entityA).position.y
        val by = mapperTransformComponent.get(entityB).position.y
        val az = mapperTransformComponent.get(entityA).position.z
        val bz = mapperTransformComponent.get(entityB).position.z

        var result = 0

        if (ay > by) {
            result = -1
        } else if (ay < by) {
            result = 1
        }

        /*if (!az.equals(0) || !bz.equals(0)) {
            if (az > bz) {
                result = 1
            } else if (az < bz) {
                result = -1
            }
        }*/

        return result
    }

}