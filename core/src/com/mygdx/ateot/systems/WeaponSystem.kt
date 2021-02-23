package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.mygdx.ateot.components.TransformComponent
import com.mygdx.ateot.components.WeaponComponent
import kotlin.math.atan2

// TODO: Add this to the PlayerControlSystem maybe? The rotation logic alone doesn't make much sense here.
// TODO: Even though later the shooting mechanics are probably better off in the PlayerControlSystem?
class WeaponSystem(private val camera: OrthographicCamera) :
    IteratingSystem(Family.all(WeaponComponent::class.java, TransformComponent::class.java).get()) {

    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val transformComponent = mapperTransformComponent.get(entity)

        val unprojectedMouseCoords = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

        val mouseX = unprojectedMouseCoords.x
        val mouseY = unprojectedMouseCoords.y

        var angle = MathUtils.radiansToDegrees * atan2(
            (mouseY - (transformComponent.position.y + transformComponent.offset.y + transformComponent.originOffset.y)).toDouble(),
            (mouseX - (transformComponent.position.x + transformComponent.offset.x + transformComponent.originOffset.x)).toDouble()
        )

        if (angle < 0f) {
            angle += 360f
        }

        transformComponent.rotation = angle.toFloat()
    }
}