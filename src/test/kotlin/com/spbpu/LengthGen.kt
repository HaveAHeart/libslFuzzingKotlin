package com.spbpu

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.randomizers.IntegerLibSLRandomizer
import com.spbpu.randomizers.StringLibSLRandomizer
import com.squareup.kotlinpoet.ClassName
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jetbrains.research.libsl.LibSL
import java.io.File

class LengthGen(type: Class<Int>?): Generator<Int>(type) {
    private val contentPath = "libslSpecs/testAnnotations/testAnnotations.lsl"
    private val libSL = LibSL("libslSpecs/spec")
    private val library = libSL.loadFromFile(File(contentPath))
    private val annotations = library.annotations
    private val annotationName = "outerSizeLimit"
    private val annotation = annotations.find { it.name == annotationName }
    //private val args = annotation?.argumentDescriptors//

    private val initAnnotationName = "outerSizeLimit"
    val params = LibSLParserUtils.getParamsFromAnnotation(annotation!!, ClassName.bestGuess(Int::class.qualifiedName!!) ).map { it.toInt() }
    private val intRandomizer = IntegerLibSLRandomizer(params.first(), params.last())
    private var erConfig: EasyRandomParameters = EasyRandomParameters()
        .randomize({ field -> field.name == initAnnotationName }, intRandomizer)
    //private val dependsOn = Utils.getStringFromExpression(args?.find { it.name == "dependsOn" }?.initialValue, "")
    //private val dependsWith = Utils.getStringFromExpression(args?.find { it.name == "dependsWith" }?.initialValue, "")
    //TODO: locate Required Generator
    private val dependsParam = URLLoaderGen /*Class.forName("${dependsOn}Gen")*/



    override fun generate(random: SourceOfRandomness, status: GenerationStatus): Int {
        val filler = random.nextBoolean()
        erConfig = erConfig.seed(random.seed())

        return URLLoaderGen.returnLastGen()?.getURL()?.length ?: intRandomizer.randomValue
    }
}