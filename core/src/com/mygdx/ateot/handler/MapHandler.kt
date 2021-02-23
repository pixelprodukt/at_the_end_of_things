package com.mygdx.ateot.handler

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.mygdx.ateot.helper.Body

class MapHandler {

    private val mapLoader = TmxMapLoader()

    lateinit var currentTiledMap: TiledMap private set

    var currentMapWidth: Int = 0
        private set
    var currentMapHeight: Int = 0
        private set

    lateinit var staticMapBodies: MutableList<Body>
        private set

    fun loadMap(mapName: String) {

        val tiledMap = mapLoader.load("maps/$mapName.tmx")

        currentTiledMap = tiledMap ?: throw Exception("Map not found")
        currentMapWidth = currentTiledMap.properties["width"] as Int * currentTiledMap.properties["tilewidth"] as Int
        currentMapHeight = currentTiledMap.properties["height"] as Int * currentTiledMap.properties["tileheight"] as Int
        staticMapBodies = initStaticBodies(currentTiledMap)
    }

    private fun initStaticBodies(map: TiledMap): MutableList<Body> {

        val rectangleList = map.layers.get("collisions")?.objects?.getByType(RectangleMapObject::class.java)
            ?: throw Exception("No collision layer found")
        val staticBodies = mutableListOf<Body>()

        rectangleList.forEach { rectangleMapObject ->

            val rect = rectangleMapObject.rectangle

            val staticBody = Body(
                Vector2(rect.x, rect.y),
                Vector2(rect.width, rect.height),
                isStatic = true
            )
            staticBodies.add(staticBody)
        }

        return staticBodies
    }
}