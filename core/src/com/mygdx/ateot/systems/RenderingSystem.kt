package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.mygdx.ateot.components.TextureComponent

import com.mygdx.ateot.components.TransformComponent

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.ateot.helper.ZComparator
import ktx.graphics.use


class RenderingSystem(private val batch: SpriteBatch) : SortedIteratingSystem(
    Family.all(TransformComponent::class.java, TextureComponent::class.java).get(),
    ZComparator()
) {

    private val renderQueue = mutableListOf<Entity>()
    private val comparator = ZComparator()

    private val mapperTextureComponent: ComponentMapper<TextureComponent> =
        ComponentMapper.getFor(TextureComponent::class.java)
    private val mapperTransformComponent: ComponentMapper<TransformComponent> =
        ComponentMapper.getFor(TransformComponent::class.java)

    val camera = OrthographicCamera(800f / 4f, 600f / 4f)

    init {
        camera.position.x = 200f / 2
        camera.position.y = 150f / 2
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        renderQueue.sortWith(comparator)
        camera.update()

        batch.projectionMatrix = camera.combined
        batch.enableBlending()

        batch.use { spriteBatch ->

            for (entity in renderQueue) {
                val textureComponent = mapperTextureComponent.get(entity)
                val transformComponent = mapperTransformComponent.get(entity)

                if (textureComponent.region == null || transformComponent.isHidden) continue

                val width: Float = textureComponent.region!!.regionWidth.toFloat()
                val height: Float = textureComponent.region!!.regionHeight.toFloat()
                val originX: Float = width / 2
                val originY: Float = height / 2
                val offsetX = transformComponent.offset.x
                val offsetY = transformComponent.offset.y
                val originOffsetX = transformComponent.originOffset.x
                val originOffsetY = transformComponent.originOffset.y

                spriteBatch.draw(
                    textureComponent.region,
                    transformComponent.position.x - originX + offsetX,
                    transformComponent.position.y - originY + offsetY,
                    originX + originOffsetX, originY + originOffsetY, width, height,
                    transformComponent.scale.x, transformComponent.scale.y, transformComponent.rotation
                )
            }
        }

        renderQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        renderQueue.add(entity!!)

        forceSort()
    }
}