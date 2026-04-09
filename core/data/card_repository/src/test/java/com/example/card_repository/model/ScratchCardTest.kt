package com.example.card_repository.model

import com.example.card_repository.domain.ScratchCard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScratchCardTest {

    @Test
    fun `UNSCRATCHED state allows scratch but not activation`() {
        val card = ScratchCard(cardState = CardState.UNSCRATCHED)

        assertTrue(card.scratchButtonEnabled())
        assertFalse(card.activateButtonEnabled())

        assertFalse(card.alreadyScratched())
        assertFalse(card.scratchInProgress())
        assertFalse(card.alreadyActivated())
        assertFalse(card.activationInProgress())
    }

    @Test
    fun `SCRATCH_IN_PROGRESS state allows scratch but not activation`() {
        val card = ScratchCard(cardState = CardState.SCRATCH_IN_PROGRESS)

        assertTrue(card.scratchButtonEnabled())
        assertFalse(card.activateButtonEnabled())

        assertTrue(card.scratchInProgress())
        assertFalse(card.alreadyScratched())
        assertFalse(card.alreadyActivated())
        assertFalse(card.activationInProgress())
    }

    @Test
    fun `SCRATCHED state allows activation but not scratch`() {
        val card = ScratchCard(cardState = CardState.SCRATCHED)

        assertFalse(card.scratchButtonEnabled())
        assertTrue(card.activateButtonEnabled())

        assertTrue(card.alreadyScratched())
        assertFalse(card.scratchInProgress())
        assertFalse(card.alreadyActivated())
        assertFalse(card.activationInProgress())
    }

    @Test
    fun `ACTIVATION_IN_PROGRESS state allows activation but not scratch`() {
        val card = ScratchCard(cardState = CardState.ACTIVATION_IN_PROGRESS)

        assertFalse(card.scratchButtonEnabled())
        assertTrue(card.activateButtonEnabled())

        assertTrue(card.activationInProgress())
        assertFalse(card.alreadyScratched())
        assertFalse(card.scratchInProgress())
        assertFalse(card.alreadyActivated())
    }

    @Test
    fun `ACTIVATED state disables both scratch and activation`() {
        val card = ScratchCard(cardState = CardState.ACTIVATED)

        assertFalse(card.scratchButtonEnabled())
        assertFalse(card.activateButtonEnabled())

        assertTrue(card.alreadyActivated())
        assertFalse(card.alreadyScratched())
        assertFalse(card.scratchInProgress())
        assertFalse(card.activationInProgress())
    }
}
