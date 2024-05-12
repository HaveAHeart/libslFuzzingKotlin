package com.spbpu.templates

import com.spbpu.Utils
import org.cornutum.regexpgen.RandomGen
import org.cornutum.regexpgen.RegExpGen
import org.cornutum.regexpgen.js.Provider
import org.cornutum.regexpgen.random.RandomBoundsGen
import org.jeasy.random.api.Randomizer
import org.jetbrains.research.libsl.nodes.Annotation

class OldStringLibSLRandomizer(initAnnotation: Annotation?): Randomizer<String>  {
    val annotation: Annotation? = initAnnotation
    private var minLength: Int = 0
    private var maxLength: Int = 50
    private var regexp: String = "*"
    private val random: RandomGen = RandomBoundsGen()
    var generator: RegExpGen

    init {
        val args = annotation?.argumentDescriptors
        minLength = Utils.getIntFromExpression(args?.find { it.name == "minLength" }?.initialValue, 0)
        maxLength = Utils.getIntFromExpression(args?.find { it.name == "maxLength" }?.initialValue, 50)
        regexp = Utils.getStringFromExpression(args?.find { it.name == "regexp" }?.initialValue, "")
        generator = Provider.forEcmaScript().matchingExact(regexp)
        println("StringGen initialized. Length: $minLength : $maxLength")
    }

    override fun getRandomValue(): String {
        return generator.generate(random, minLength, maxLength)
    }
}