package com.spbpu

import com.spbpu.FuzzTestsGenerator.Companion.makeFuzzTestsForLibrary
import com.spbpu.GeneratorGenerator.Companion.makeGeneratorsForLibrary
import com.spbpu.Utils.Companion.cleanGenFolder
import org.jetbrains.research.libsl.type.StructuredType
import kotlin.io.path.Path

/*
    TODO:
    > Replace methods via javaAgent
        - get correct code piece
            - random initialization
            - gen initialization
            - gen generation
            - returning gen result
        - find required class\method
            - get className from libSL type
            - get class\method from automaton\fun
            - get the transformer https://www.baeldung.com/java-instrumentation
            - replace with premain
        - test via 'java -javaagent:agent.jar -jar application.jar'
 */


fun main(args: Array<String>) {

    val library = LibSLParserUtils.getLibrary("libslSpecs/spec", "libslSpecs/testAnnotations/testAnnotations.lsl")

    val types = library.resolvedTypes

    types.forEach { println(it) }

    val generators = makeGeneratorsForLibrary(library)
    val tests = makeFuzzTestsForLibrary(library)

    cleanGenFolder()
    val basePath = Path("src/test/kotlin/")
    generators.forEach { it.writeTo(basePath.toFile()) }
    tests.forEach { it.writeTo(basePath.toFile()) }

    val person = Person("DIO", 210F, false)

    

    println(person)
}

//mvn jqf:fuzz -Dclass=com.spbpu.FuzzingTest -Dmethod=testURLLoaderWithGenerator -Dtime=10s

