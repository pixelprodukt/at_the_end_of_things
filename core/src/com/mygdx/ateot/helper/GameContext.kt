package com.mygdx.ateot.helper

import com.badlogic.ashley.core.PooledEngine
import com.mygdx.ateot.handler.AssetHandler
import com.mygdx.ateot.handler.EventHandler
import com.mygdx.ateot.handler.InputHandler
import com.mygdx.ateot.handler.MapHandler

/**
 * Class for holding gamestate as well as services or
 * "global" variables or in general as glue code
 */
class GameContext {

    val eventHandler = EventHandler()
    val assetHandler = AssetHandler()
    val inputHandler = InputHandler()

    val engine = PooledEngine()
    val entityFactory = EntityFactory(engine, assetHandler)

    val mapHandler = MapHandler(entityFactory).apply { loadMap("ateot_testmap") }
}