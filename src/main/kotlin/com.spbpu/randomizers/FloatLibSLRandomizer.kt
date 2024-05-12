package com.spbpu.randomizers

import com.spbpu.Utils
import org.jeasy.random.api.Randomizer
import org.jetbrains.research.libsl.nodes.Annotation
import kotlin.random.Random

class FloatLibSLRandomizer(initMinValue: Float = Float.MIN_VALUE, initMaxValue: Float = Float.MAX_VALUE): Randomizer<Float> {
    private var minValue: Float = initMinValue
    private var maxValue: Float = initMaxValue
    private var lastGenValue: Float = 0F

    init {
        println("FloatGen initialized. Range: $minValue : $maxValue")
    }

    fun getLastGenValue(): Float = lastGenValue

    override fun getRandomValue(): Float {
        val res = minValue + Random.nextFloat() * (maxValue - minValue)
        lastGenValue = res
        return lastGenValue
    }
}