package com.spbpu

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.randomizers.StringLibSLRandomizer
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jetbrains.research.libsl.LibSL
import java.io.File

class URLLoaderGen(type: Class<URLLoader>?): Generator<URLLoader>(type) {
    companion object LastGenVal {
        var lastGenVal: URLLoader? = null
        fun returnLastGen(): URLLoader? {
            return lastGenVal
        }

    }
    private val contentPath = "libslSpecs/testAnnotations/testAnnotations.lsl"
    private val libSL = LibSL("libslSpecs/spec")
    private val library = libSL.loadFromFile(File(contentPath))
    private val annotationName = "URL"
    private val annotation = library.annotations.find { it.name == "returnURL" }
    private val initAnnotationName = "url"

    //private val params = LibSLParserUtils.getParamsFromAnnotation()
    private val erConfig: EasyRandomParameters = EasyRandomParameters()
        .randomize({ field -> field.name == initAnnotationName }, StringLibSLRandomizer(1, 50, "[abc]+"))

    override fun generate(random: SourceOfRandomness, status: GenerationStatus): URLLoader {
        val filler = random.nextBoolean()
        status.setValue(GenerationStatus.Key("lastGenURLLoader", URLLoader::class.java), lastGenVal)
        val generatedValue = EasyRandom(erConfig).nextObject(URLLoader::class.java)
        lastGenVal = generatedValue
        return generatedValue
    }
}