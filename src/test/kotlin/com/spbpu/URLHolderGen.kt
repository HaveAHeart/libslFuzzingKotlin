package com.spbpu

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.randomizers.StringLibSLRandomizer
import com.squareup.kotlinpoet.ClassName
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jetbrains.research.libsl.LibSL
import java.io.File

class URLHolderGen(type: Class<URLHolder>?): Generator<URLHolder>(type) {
    private val contentPath = "libslSpecs/testAnnotations/testAnnotations.lsl"
    private val libSL = LibSL("libslSpecs/spec")
    private val library = libSL.loadFromFile(File(contentPath))
    private val annotationName = "returnURL"
    private val annotation = library.annotations.find { it.name == annotationName }
    private val fieldName = "url"
    private val params = LibSLParserUtils.getParamsFromAnnotation(annotation!!, ClassName.bestGuess(String::class.qualifiedName!!) )
    private val erConfig: EasyRandomParameters = EasyRandomParameters().randomize(
        { field -> field.name == fieldName },
        StringLibSLRandomizer(params[0].toInt(), params[1].toInt(), params[2])
    )

    override fun generate(random: SourceOfRandomness, status: GenerationStatus): URLHolder {
        val filler = random.nextBoolean()
        val generatedValue = EasyRandom(erConfig).nextObject(URLHolder::class.java)
        return generatedValue
    }
}