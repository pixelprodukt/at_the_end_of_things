package com.mygdx.ateot.events

interface GameEventListener<T : GameEvent<out Any>> {
    fun handle(gameEvent: T)
}