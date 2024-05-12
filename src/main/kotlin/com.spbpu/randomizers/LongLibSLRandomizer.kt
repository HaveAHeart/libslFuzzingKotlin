package com.spbpu.randomizers

import org.jeasy.random.api.Randomizer

class LongLibSLRandomizer(initMinValue: Long = Long.MIN_VALUE, initMaxValue: Long = Long.MAX_VALUE): Randomizer<Long> {
    private var minValue: Long = initMinValue
    private var maxValue: Long = initMaxValue
    private var lastGenValue: Long = 0

    init {
        println("LongGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Long = lastGenValue

    override fun getRandomValue(): Long {
        val res = (minValue..maxValue).random()
        lastGenValue = res
        return res
    }
}