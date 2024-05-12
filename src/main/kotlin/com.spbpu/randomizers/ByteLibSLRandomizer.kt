package com.spbpu.randomizers

import org.jeasy.random.api.Randomizer

class ByteLibSLRandomizer(initMinValue: Byte = Byte.MIN_VALUE, initMaxValue: Byte = Byte.MAX_VALUE): Randomizer<Byte> {
    private var minValue: Byte = initMinValue
    private var maxValue: Byte = initMaxValue
    private var lastGenValue: Byte = 0

    init {
        println("ByteGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Byte = lastGenValue

    override fun getRandomValue(): Byte {
        val res = (minValue..maxValue).random().toByte()
        lastGenValue = res
        return res
    }
}