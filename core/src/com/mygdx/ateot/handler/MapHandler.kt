package com.mygdx.ateot.handler

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2
import com.mygdx.ateot.helper.Body
import com.mygdx.ateot.helper.EntityFactory

class MapHandler(private val entityFactory: EntityFactory) {

    private val mapLoader = TmxMapLoader()

    lateinit var currentTiledMap: TiledMap private set

    var currentMapWidth: Int = 0
        private set
    var currentMapHeight: Int = 0
        private set

    //val staticMapBodies = mutableListOf<Body>()
    //val staticWallBodies = mutableListOf<Body>()

    fun loadMap(mapName: String) {

        //staticMapBodies.clear()
        //staticWallBodies.clear()

        val tiledMap = mapLoader.load("maps/$mapName.tmx")

        currentTiledMap = tiledMap ?: throw Exception("Map not found")
        currentMapWidth = currentTiledMap.properties["width"] as Int * currentTiledMap.properties["tilewidth"] as Int
        currentMapHeight = currentTiledMap.properties["height"] as Int * currentTiledMap.properties["tileheight"] as Int
        initStaticBodies(currentTiledMap)
        initStaticObjects(currentTiledMap)
    }

    private fun initStaticObjects(map: TiledMap) {

        val rectangleList = map.layers.get("gameobjects")?.objects?.getByType(TiledMapTileMapObject::class.java)
            ?: throw Exception("No gameobjects layer found")

        rectangleList.forEach { mapObject ->

            if (mapObject.properties["type"] == "explosive_barrel") {
                entityFactory.createExplosiveBarrel(Vector2(mapObject.x + (mapObject.properties["width"] as Float / 2), mapObject.y + (mapObject.properties["height"] as Float / 2)))
            }

            if (mapObject.properties["type"] == "fleshblob") {
                entityFactory.createFleshblob(Vector2(mapObject.x + (mapObject.properties["width"] as Float / 2), mapObject.y + (mapObject.properties["height"] as Float / 2)))
            }
        }
    }

    private fun initStaticBodies(map: TiledMap) {

        val rectangleList = map.layers.get("collisions")?.objects?.getByType(RectangleMapObject::class.java)
                ?: throw Exception("No collision layer found")

        rectangleList.forEach { rectangleMapObject ->

            val rect = rectangleMapObject.rectangle

            if (rectangleMapObject.properties["type"] == "wall_collision") {
                entityFactory.createCollisionEntity(Vector2(rect.x, rect.y), Vector2(rect.width, rect.height), true)
            }

            if (rectangleMapObject.properties["type"] == "floor_collision") {
                entityFactory.createCollisionEntity(Vector2(rect.x, rect.y), Vector2(rect.width, rect.height), false)
            }
        }
    }
}