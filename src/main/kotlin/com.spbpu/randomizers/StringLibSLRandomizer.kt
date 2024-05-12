package com.spbpu.randomizers

import org.cornutum.regexpgen.RandomGen
import org.cornutum.regexpgen.RegExpGen
import org.cornutum.regexpgen.js.Provider
import org.cornutum.regexpgen.random.RandomBoundsGen
import org.jeasy.random.api.Randomizer

class StringLibSLRandomizer(initMinLength: Int = 1, initMaxLength: Int = 50, initRegexp: String = ".*"): Randomizer<String>  {
    private val random: RandomGen = RandomBoundsGen()
    var generator: RegExpGen = Provider.forEcmaScript().matchingExact(initRegexp)
    private val minLength = initMinLength
    private val maxLength = initMaxLength
    private var lastGenValue: String = ""

    fun getLastGenValue(): String = lastGenValue

    override fun getRandomValue(): String {
        val res = generator.generate(random, minLength, maxLength)
        lastGenValue = res
        return res
    }
}
