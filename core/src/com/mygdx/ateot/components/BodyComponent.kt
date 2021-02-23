package com.mygdx.ateot.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.mygdx.ateot.helper.Body

class BodyComponent : Component {
    val body: Body = Body(Vector2(0.0f, 0.0f), Vector2(16.0f, 16.0f), Vector2(0f, 0f))
}
