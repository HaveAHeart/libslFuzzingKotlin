package com.spbpu.randomizers

import org.jeasy.random.api.Randomizer

class ShortLibSLRandomizer(initMinValue: Short = Short.MIN_VALUE, initMaxValue: Short = Short.MAX_VALUE): Randomizer<Short> {
    private var minValue: Short = initMinValue
    private var lastGenValue: Short = 0
    private var maxValue: Short = initMaxValue

    init {
        println("ShortGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Short = lastGenValue

    override fun getRandomValue(): Short {
        val res = (minValue..maxValue).random().toShort()
        lastGenValue = res
        return res
    }
}