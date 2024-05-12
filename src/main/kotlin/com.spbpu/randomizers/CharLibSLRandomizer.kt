package com.spbpu.randomizers

import org.jeasy.random.api.Randomizer
import kotlin.random.Random

class CharLibSLRandomizer(initAvailibleChars: String?): Randomizer<Char> {
    private val availibleChars = initAvailibleChars?.toSet()
    private var lastGenValue: Char = '_'

    init {
        println("CharGen initialized. Range: ${availibleChars ?: "all chars"}")
    }

    fun getLastGenValue(): Char = lastGenValue

    override fun getRandomValue(): Char {
        val res = availibleChars?.random() ?: Random.nextInt().toChar()
        lastGenValue = res
        return lastGenValue
    }
}