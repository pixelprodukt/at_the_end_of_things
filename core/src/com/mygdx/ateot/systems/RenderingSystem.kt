package com.mygdx.ateot.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.mygdx.ateot.components.TextureComponent

import com.mygdx.ateot.components.TransformComponent

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.mygdx.ateot.components.HitpointsComponent
import com.mygdx.ateot.helper.RenderingOrderTransformComparator
import ktx.graphics.use


class RenderingSystem(private val batch: SpriteBatch) : SortedIteratingSystem(
    Family.all(TransformComponent::class.java, TextureComponent::class.java).get(),
    RenderingOrderTransformComparator()
) {

    private val renderQueue = mutableListOf<Entity>()
    private val comparator = RenderingOrderTransformComparator()

    private val mapperTextureComponent = ComponentMapper.getFor(TextureComponent::class.java)
    private val mapperTransformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    private val mapperHitpointsComponent = ComponentMapper.getFor(HitpointsComponent::class.java)

    val camera = OrthographicCamera(800f / 4f, 600f / 4f)

    val vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "uniform mat4 u_projTrans;\n" + "varying vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "\n" + "void main()\n" + "{\n" + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}\n"
    val fragmentShader = "#ifdef GL_ES\n" + "#define LOWP lowp\n" + "precision mediump float;\n" + "#else\n" + "#define LOWP \n" + "#endif\n" + "varying LOWP vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n" + "void main()\n" + "{\n" + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords).a;\n" + "}"
    val shader = ShaderProgram(vertexShader, fragmentShader)

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

                /**
                 * blink effect after being hit
                 */
                if (mapperHitpointsComponent.get(entity) != null && mapperHitpointsComponent.get(entity).isInvincible) {

                    //Gdx.app.log("RenderingSystem", "${mapperHitpointsComponent.get(entity).timeSinceLastHit % 0.4}")
                    if (mapperHitpointsComponent.get(entity).timeSinceLastHit % 0.4f > 0.2f) {
                        spriteBatch.shader = shader
                    } else {
                        spriteBatch.shader = null
                    }
                }

                spriteBatch.draw(
                    textureComponent.region,
                    transformComponent.position.x - originX + offsetX,
                    transformComponent.position.y - originY + offsetY,
                    originX + originOffsetX, originY + originOffsetY, width, height,
                    transformComponent.scale.x, transformComponent.scale.y, transformComponent.rotation
                )

                //spriteBatch.color = Color.WHITE
                spriteBatch.shader = null
            }
        }

        renderQueue.clear()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        renderQueue.add(entity!!)

        forceSort()
    }
}