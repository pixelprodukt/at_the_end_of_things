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

    val staticMapBodies = mutableListOf<Body>()
    val staticWallBodies = mutableListOf<Body>()

    fun loadMap(mapName: String) {

        staticMapBodies.clear()
        staticWallBodies.clear()

        val tiledMap = mapLoader.load("maps/$mapName.tmx")

        currentTiledMap = tiledMap ?: throw Exception("Map not found")
        currentMapWidth = currentTiledMap.properties["width"] as Int * currentTiledMap.properties["tilewidth"] as Int
        currentMapHeight = currentTiledMap.properties["height"] as Int * currentTiledMap.properties["tileheight"] as Int
        initStaticBodies(currentTiledMap)
    }

    private fun initStaticBodies(map: TiledMap) {

        val rectangleList = map.layers.get("collisions")?.objects?.getByType(RectangleMapObject::class.java)
                ?: throw Exception("No collision layer found")

        rectangleList.forEach { rectangleMapObject ->

            val rect = rectangleMapObject.rectangle

            val staticBody = Body(
                    Vector2(rect.x, rect.y),
                    Vector2(rect.width, rect.height),
                    isStatic = true
            )

            if (rectangleMapObject.properties["isWall"] !== null) {
                if (rectangleMapObject.properties["isWall"]!! as Boolean) {
                    staticWallBodies.add(staticBody)
                }
            }


            staticMapBodies.add(staticBody)
        }
    }
}