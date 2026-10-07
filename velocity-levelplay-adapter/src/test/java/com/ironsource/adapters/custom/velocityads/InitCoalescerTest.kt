package com.ironsource.adapters.custom.velocityads

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InitCoalescerTest {
    @Test
    fun `one caller wins and all callers receive completion in order`() {
        val coalescer = InitCoalescer<Boolean>()
        val results = mutableListOf<Int>()

        assertTrue(coalescer.claim { results += 1 })
        assertFalse(coalescer.claim { results += 2 })
        assertFalse(coalescer.claim { results += 3 })

        coalescer.complete(true)

        assertEquals(listOf(1, 2, 3), results)
        assertFalse(coalescer.isClaimed)
    }

    @Test
    fun `completion permits a new initialization attempt`() {
        val coalescer = InitCoalescer<Boolean>()
        coalescer.claim { }
        coalescer.complete(false)

        assertTrue(coalescer.claim { })
    }
}
