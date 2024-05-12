package com.spbpu

import com.pholser.junit.quickcheck.From
import com.spbpu.GeneratorGenerator.Companion.makeGeneratorName
import com.spbpu.Utils.Companion.resolveLibSLTypeToClassName
import com.squareup.kotlinpoet.*
import edu.berkeley.cs.jqf.fuzz.Fuzz
import edu.berkeley.cs.jqf.fuzz.JQF
import org.jetbrains.research.libsl.nodes.*
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.Function
import org.jetbrains.research.libsl.type.Type
import org.junit.Assert
import org.junit.runner.RunWith
import kotlin.reflect.KClass

class FuzzTestsGenerator {
    companion object {
        fun makeFuzzTestsForLibrary(library: Library): List<FileSpec> {
            val types = library.resolvedTypes
            val genInfo = mutableListOf<FileSpec>()
            library.automata.forEach { automaton ->
                automaton.functions.forEach { func ->
                    genInfo.add(makeFuzzTestForFunction(types, automaton.name, func.name, func))
                }
            }
            return genInfo
        }

        fun makeFuzzTestName(nameParts: List<String>): String {
            return nameParts.joinToString("_", postfix = "_FuzzTest")
        }

        fun makeFuzzTestForFunction(types: List<Type>, automatonName: String, methodName: String, funcNode: Function): FileSpec {
            val testName = makeFuzzTestName(listOf(automatonName, methodName))
            val builder = FileSpec.builder("com.spbpu.genFuzzing.fuzzTests", testName)

            val typeBuilder = CodeGenerationUtils.makeType(testName).toBuilder()

            typeBuilder.addAnnotation(getRunWithAnnotation())

            val funcTest = makeFunctionsTest(types, automatonName, methodName, funcNode, builder)
            typeBuilder.addFunctions(funcTest)

            builder.addType(typeBuilder.build())
            return builder.build()
        }

        fun makeFunctionsTest(types: List<Type>, automatonName: String, methodName: String, funcNode: Function, fileBuilder: FileSpec.Builder): List<FunSpec> {
            val argGenName = makeGeneratorName(listOf(automatonName, methodName))
            val fromAnnotation = getFromAnnotation(argGenName)
            val argType = resolveLibSLTypeToClassName(types, funcNode.returnType)
            val argName = "genResult"
            fileBuilder.addImport("com.spbpu.genFuzzing.generators", argGenName)

            val param = ParameterSpec.builder(argName, argType)
                .addAnnotation(fromAnnotation)
                .build()

            val asserts = funcNode.contracts
            asserts.removeAll { it.kind != ContractKind.ENSURES }

            val assertFunctions = mutableListOf<FunSpec>()
            asserts.forEach { assert ->
                val assertName = assert.name ?: "defaultAssertName"
                val assertContent = assert.expression.dumpToString()
                val assertTest = FunSpec.builder(assertName)
                    .addAnnotation(getFuzzAnnotation())
                    .addParameter(param)
                    .addCode(CodeBlock.of("%T.assertTrue($assertContent)", Assert::class))
                    .build()
                assertFunctions.add(assertTest)
            }

            return assertFunctions
        }

        fun getFillerTest(): FunSpec {
            val simpleParam = ParameterSpec.builder("simpleString", String::class).build()
            val fillerTest = FunSpec.builder("alwaysPasses")
                .addAnnotation(getFuzzAnnotation())
                .addParameter(simpleParam)
                .addCode(CodeBlock.of("%T.assertTrue(\"${simpleParam.name} is not empty!\".isNotEmpty())", Assert::class))
                .build()

            return fillerTest
        }

        fun getFuzzAnnotation(): AnnotationSpec {
            val fuzzAnnotation = AnnotationSpec.builder(Fuzz::class)
                .build()
            return fuzzAnnotation
        }

        fun getFromAnnotation(genName: String): AnnotationSpec {
            val fromAnnotation = AnnotationSpec.builder(From::class)
                .addMember(CodeBlock.of("$genName::class"))
                .build()
            return fromAnnotation
        }

        fun getRunWithAnnotation(): AnnotationSpec {
            val runWithAnnotation = AnnotationSpec.builder(RunWith::class)
                .addMember(CodeBlock.of("%T::class", JQF::class))
                .build()
            return runWithAnnotation
        }
    }
}