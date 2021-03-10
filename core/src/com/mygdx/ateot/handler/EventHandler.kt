package com.mygdx.ateot.handler

import com.mygdx.ateot.enums.GameEventType
import com.mygdx.ateot.events.GameEvent
import com.mygdx.ateot.events.GameEventListener
import kotlin.reflect.KClass

class EventHandler {

    val listeners: MutableMap<KClass<out GameEvent<out Any>>, MutableList<GameEventListener<out GameEvent<out Any>>>> =
        mutableMapOf()

    inline fun <reified T : GameEvent<out Any>> addListener(listener: GameEventListener<T>) {
        val eventClass = T::class
        val eventListeners: MutableList<GameEventListener<out GameEvent<out Any>>> =
            listeners.getOrPut(eventClass) { mutableListOf() }
        eventListeners.add(listener)
    }

    inline fun <reified T : GameEvent<out Any>> publish(gameEvent: T) {
        listeners[T::class]?.asSequence()
            ?.filterIsInstance<GameEventListener<T>>()
            ?.forEach { it.handle(gameEvent) }
    }

    /*val listeners: MutableMap<GameEventType, MutableList<(GameEvent<out Any>) -> Unit>> = mutableMapOf()

    fun addListener(eventType: GameEventType, listener: (GameEvent<out Any>) -> Unit) {
        val eventListeners: MutableList<(GameEvent<out Any>) -> Unit> = listeners.getOrPut(eventType) { mutableListOf() }
        eventListeners.add(listener)
    }

    fun <T : GameEvent<out Any>> publish(eventType: GameEventType, gameEvent: GameEvent<out Any>) {
        listeners[eventType]?.forEach { listener -> listener(gameEvent as T) }
    }*/

    /*
    val listeners: MutableMap<KClass<out Any>, MutableList<GameEventListener<out GameEvent<out Any>>>> = mutableMapOf()

    inline fun <reified T : GameEvent<out Any>> addListener(listener: GameEventListener<T>) {
        val eventClass = T::class
        val eventListeners: MutableList<GameEventListener<out GameEvent<out Any>>> = listeners.getOrPut(eventClass) { mutableListOf() }
        eventListeners.add(listener)
    }

    inline fun <reified T : GameEvent<out Any>> publish(gameEvent: T) {
        listeners[gameEvent::class]?.asSequence()
            ?.filterIsInstance<GameEventListener<T>>()
            ?.forEach { it.handle(gameEvent) }
    }
     */
}