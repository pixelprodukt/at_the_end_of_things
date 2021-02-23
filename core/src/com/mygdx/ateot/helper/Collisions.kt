package com.mygdx.ateot.helper

import com.badlogic.gdx.math.Vector2
import kotlin.math.abs
import kotlin.math.sqrt

class Body(
    val position: Vector2,
    val size: Vector2,
    val velocity: Vector2 = Vector2(0f, 0f),
    val isSensor: Boolean = false,
    val isStatic: Boolean = false,
    val offset: Vector2 = Vector2(0f, 0f),
) {
    val center get() = Vector2(position.x + (size.x / 2), position.y + (size.y / 2))
}

fun resolveCollision(b1: Body, b2: Body) {

    val seperationVector = testBodyAgainstBody(b1, b2)

    if (seperationVector != null) {
        solveCollision(b1, b2, seperationVector)
    }
}

fun testBodyAgainstBody(a: Body, b: Body): Vector2? {
    // distance between the bodies
    val distanceX = (a.position.x + a.offset.x + (a.size.x / 2)) - (b.position.x + b.offset.x + (b.size.x / 2))
    val distanceY = (a.position.y + a.offset.y + (a.size.y / 2)) - (b.position.y + b.offset.y + (b.size.y / 2))

    val adx = abs(distanceX)
    val ady = abs(distanceY)

    // sum of the extends
    val shw = (a.size.x / 2) + (b.size.x / 2)
    val shh = (a.size.y / 2) + (b.size.y / 2)

    //Gdx.app.log("CollisionTest", "${!(adx >= shw || ady >= shh)}")
    if (adx >= shw || ady >= shh) {
        // no intersection
        return null
    } else {
        // intersection

        // shortest seperation
        var sx = shw - adx
        var sy = shh - ady

        // ignore longer axis
        if (sx < sy) {
            if (sx > 0) sy = 0f
        } else {
            if (sy > 0) sx = 0f
        }

        // correct sign
        if (distanceX < 0) sx = -sx
        if (distanceY < 0) sy = -sy

        return Vector2(sx, sy)
    }
}

fun solveCollision(a: Body, b: Body, sVec: Vector2) {
    // find the collision normal
    val d = sqrt((sVec.x * sVec.x).toDouble() + (sVec.y * sVec.y).toDouble())
    val nx = sVec.x / d
    val ny = sVec.y / d

    // relative velocity
    val vx = a.velocity.x - b.velocity.x
    val vy = a.velocity.y - b.velocity.y

    // penetration speed
    val ps = (vx * nx) + (vy * ny)

    // objects moving towards?
    if (ps <= 0) {
        a.position.x = a.position.x + sVec.x
        a.position.y = a.position.y + sVec.y
    }
}

fun intersect(b1: Body, b2: Body): Boolean {
    return (b1.position.x < b2.position.x + b2.size.x &&
            b1.position.x + b1.size.x > b2.position.x &&
            b1.position.y < b2.position.y + b2.size.y &&
            b1.position.y + b1.size.y > b2.position.y)
}