package com.ironsource.adapters.custom.velocityads

internal class InitCoalescer<Outcome> {
    var isClaimed: Boolean = false
        private set

    private val pendingHandlers = mutableListOf<(Outcome) -> Unit>()

    fun claim(handler: (Outcome) -> Unit): Boolean {
        pendingHandlers.add(handler)
        if (isClaimed) return false
        isClaimed = true
        return true
    }

    fun complete(outcome: Outcome) {
        val handlers = pendingHandlers.toList()
        pendingHandlers.clear()
        isClaimed = false
        handlers.forEach { it(outcome) }
    }
}
