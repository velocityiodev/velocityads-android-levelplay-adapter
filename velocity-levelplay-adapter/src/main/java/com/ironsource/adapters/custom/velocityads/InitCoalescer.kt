package com.ironsource.adapters.custom.velocityads

internal class InitCoalescer<Outcome> {
    @Volatile
    var isClaimed: Boolean = false
        private set

    private val pendingHandlers = mutableListOf<(Outcome) -> Unit>()

    @Synchronized
    fun claim(handler: (Outcome) -> Unit): Boolean {
        pendingHandlers.add(handler)
        if (isClaimed) return false
        isClaimed = true
        return true
    }

    fun complete(outcome: Outcome) {
        val handlers =
            synchronized(this) {
                val currentHandlers = pendingHandlers.toList()
                pendingHandlers.clear()
                isClaimed = false
                currentHandlers
            }
        handlers.forEach { it(outcome) }
    }
}
