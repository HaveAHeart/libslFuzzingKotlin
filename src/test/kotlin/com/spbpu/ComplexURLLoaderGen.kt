package com.spbpu

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.randomizers.StringLibSLRandomizer
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jetbrains.research.libsl.LibSL
import java.io.File

class ComplexURLLoaderGen(type: Class<ComplexURLLoader>?): Generator<ComplexURLLoader>(type) {
    private lateinit var erConfig: EasyRandomParameters
    init {
        val contentPath = "libslSpecs/testAnnotations/testAnnotations.lsl"
        val libSL = LibSL("libslSpecs/spec")
        val library = libSL.loadFromFile(File(contentPath))
        val annotations = library.annotations
        val annotationName = "URL"
        val initAnnotationName = "url"
        erConfig = EasyRandomParameters()
            .randomize({ field -> field.name == initAnnotationName }, StringLibSLRandomizer(1, 50, "[abc]+"))
    }


    override fun generate(random: SourceOfRandomness?, ignore: GenerationStatus?): ComplexURLLoader {
        erConfig = erConfig.seed(random?.seed() ?: 0)
        val filler = random?.nextBoolean() ?: false
        return EasyRandom(erConfig).nextObject(ComplexURLLoader::class.java)
    }
}