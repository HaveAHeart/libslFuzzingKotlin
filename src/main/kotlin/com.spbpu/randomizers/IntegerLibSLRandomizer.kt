package com.spbpu.randomizers

import com.spbpu.Utils
import org.jeasy.random.api.Randomizer
import org.jetbrains.research.libsl.nodes.Annotation

class IntegerLibSLRandomizer(initMinValue: Int = Int.MIN_VALUE, initMaxValue: Int = Int.MAX_VALUE): Randomizer<Int> {
    private var minValue: Int = initMinValue
    private var maxValue: Int = initMaxValue
    private var lastGenValue: Int = 0

    init {
        println("IntGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Int = lastGenValue

    override fun getRandomValue(): Int {
        val res = (minValue..maxValue).random()
        lastGenValue = res
        return res
    }
}