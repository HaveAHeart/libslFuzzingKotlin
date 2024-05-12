package com.spbpu.randomizers

import com.spbpu.Utils
import org.jeasy.random.api.Randomizer
import org.jetbrains.research.libsl.nodes.Annotation
import kotlin.random.Random

class DoubleLibSLRandomizer(initMinValue: Double = Double.MIN_VALUE, initMaxValue: Double = Double.MAX_VALUE): Randomizer<Double> {
    private var minValue: Double = initMinValue
    private var maxValue: Double = initMaxValue
    private var lastGenValue: Double = 0.toDouble()

    init {
        println("DoubleGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Double = lastGenValue

    override fun getRandomValue(): Double {
        val res = minValue + Random.nextDouble() * (maxValue - minValue)
        lastGenValue = res
        return lastGenValue
    }
}