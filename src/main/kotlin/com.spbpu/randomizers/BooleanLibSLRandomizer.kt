package com.spbpu.randomizers

import org.jeasy.random.api.Randomizer

class BooleanLibSLRandomizer(initDefaultValue: Boolean? = null): Randomizer<Boolean> {
    private val defaultValue = initDefaultValue
    private var lastGenValue: Boolean = false

    init {
        println("BooleanGen initialized. Default value: ${defaultValue ?: "not specified"}")
    }

    fun getLastGenValue(): Boolean = lastGenValue

    override fun getRandomValue(): Boolean {
        val res = defaultValue ?: ((0..1).random() == 0)
        lastGenValue = res
        return res
    }
}