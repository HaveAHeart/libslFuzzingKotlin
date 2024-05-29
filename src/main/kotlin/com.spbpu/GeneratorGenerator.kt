package com.spbpu

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.CodeGenerationUtils.Companion.makeFunction
import com.spbpu.CodeGenerationUtils.Companion.makeProperty
import com.spbpu.CodeGenerationUtils.Companion.makeType
import com.spbpu.LibSLParserUtils.Companion.classToRandomizerClass
import com.spbpu.LibSLParserUtils.Companion.getDependableParamsFromAnnotation
import com.spbpu.LibSLParserUtils.Companion.getFieldClassFromAnnotation
import com.spbpu.LibSLParserUtils.Companion.getParamsFromAnnotation
import com.spbpu.LibSLParserUtils.Companion.getRandomizedTypes
import com.spbpu.LibSLParserUtils.Companion.isDependableAnnotation
import com.spbpu.LibSLParserUtils.Companion.resolveLibSLTypeToClassName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jeasy.random.EasyRandomParameters
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.Automaton
import org.jetbrains.research.libsl.nodes.Function
import org.jetbrains.research.libsl.nodes.Library
import org.jetbrains.research.libsl.type.Type

class GeneratorGenerator {
    companion object {
        fun makeGeneratorsForLibrary(library: Library): List<FileSpec> {
            val annotations = library.annotations
            val types = library.resolvedTypes
            val genInfo = mutableListOf<FileSpec>()
            library.automata.forEach { automaton ->
                automaton.functions.forEach { func ->
                    genInfo.add(makeGeneratorFromLibSLNode(types, automaton, func, annotations))
                }
            }
            return genInfo
        }

        fun makeGeneratorFromLibSLNode(types: List<Type>, automaton: Automaton, func: Function, annotations: List<Annotation>): FileSpec {
            val autoName = automaton.name
            val funcName = func.name
            val returnType = func.returnType
            val argClass = resolveLibSLTypeToClassName(types, returnType)
            val funcAnnotationNames = func.annotationUsages.map { it.annotationReference.name }
            val funcAnnotations = annotations.filter { it.name in funcAnnotationNames }

            return makeGenerator(autoName, funcName, argClass, funcAnnotations)
        }

        fun makePropertyRandomizer(varType: ClassName, varName: String, genParams: List<String>): PropertySpec? {
            val inParams = if (varType.canonicalName.contains("Float")) { genParams.map { "${it}F" } } else { genParams }
            val strParams = inParams.joinToString(separator = ", ")

            val randClassName = classToRandomizerClass[varType.canonicalName] ?: return null

            val randType = ClassName("com.spbpu.randomizers", randClassName)
            val prop = PropertySpec.builder("${varName}Randomizer", randType)
                .initializer("%T($strParams)", randType)
                .addModifiers(KModifier.PRIVATE)
                .mutable(false)
                .build()
            return prop

        }

        fun makeDependablePropertyRandomizer(varType: ClassName, varName: String, genParams: List<String>, generatedAnnotations: List<Pair<String, PropertySpec?>>): PropertySpec? {
            val dependsOn = genParams.first()
            val dependsWith = genParams[1]

            val dependsOnProperty = generatedAnnotations.find { it.first ==  dependsOn }?.second ?: return null
            val randType = LambdaTypeName.get(returnType = varType)

            val prop = PropertySpec.builder("${varName}Randomizer", randType)
                .initializer("{ %N.getLastGenValue().$dependsWith }", dependsOnProperty)
                .addModifiers(KModifier.PRIVATE)
                .mutable(false)
                .build()
            return prop
        }

        fun makePropertyRandomizerFromAnnotation(annotation: Annotation, generatedAnnotations: List<Pair<String, PropertySpec?>>): Pair<String, PropertySpec?> {
            val varType = ClassName.bestGuess(getFieldClassFromAnnotation(annotation))

            val isDependable = isDependableAnnotation(annotation)
            val params = if (isDependable) { getDependableParamsFromAnnotation(annotation).drop(1) }
                else { getParamsFromAnnotation(annotation, varType).drop(1) }
            val varName = if (isDependable) { getDependableParamsFromAnnotation(annotation).first() }
            else { getParamsFromAnnotation(annotation, varType).first() }
            val currRandomizer = if (isDependable) { makeDependablePropertyRandomizer(varType, varName, params, generatedAnnotations) }
            else { makePropertyRandomizer(varType, varName, params) }

            return Pair(varName, currRandomizer)
        }


        fun makeGenerator(automatonName: String, methodName: String, varType: ClassName, annotations: List<Annotation>) : FileSpec {
            val generatorName = makeGeneratorName(listOf(automatonName, methodName))
            val builder = FileSpec.builder("com.spbpu.genFuzzing.generators", generatorName)

            val paramClass = ClassName.bestGuess("java.lang.Class").parameterizedBy(varType)

            val typeBuilder = makeType(generatorName).toBuilder()
            val constructor = FunSpec.constructorBuilder()
                .addParameter("type", paramClass)
                .build()
            typeBuilder.primaryConstructor(constructor)

            val superClass = ClassName.bestGuess(Generator::class.qualifiedName!!).parameterizedBy(varType)
            typeBuilder.superclass(superClass)
            typeBuilder.addSuperclassConstructorParameter("type")

            val independentRandomizers = annotations
                .filter { !isDependableAnnotation(it) }
                .map { makePropertyRandomizerFromAnnotation(it, listOf()) }
            val dependableRandomizers = annotations
                .filter { isDependableAnnotation(it) }
                .map { makePropertyRandomizerFromAnnotation(it, independentRandomizers) }
            val randomizers = (independentRandomizers + dependableRandomizers).filter { it.second != null }

            typeBuilder.addProperties(randomizers.map { it.second!! })

            val baseCode = StringBuilder("EasyRandomParameters()")
            randomizers.forEach {
                val predicate = "{ field -> field.name == \"${it.first}\" }"
                val randomize = ".randomize($predicate, ${it.second!!.name})"
                baseCode.append(randomize)
            }

            val erConfig = PropertySpec.builder("erConfig", EasyRandomParameters::class)
                .initializer(CodeBlock.of(baseCode.toString()))
                .addModifiers(KModifier.PRIVATE)
                .mutable(false)
                .build()
            typeBuilder.addProperty(erConfig)

            val varTypeNullable = varType.copy(nullable = true)

            val lastGenVal = makeProperty(KModifier.PRIVATE, true, "lastGenVal", varTypeNullable, "null")

            val genValGetter = makeFunction(false, "getLastGenVal", CodeBlock.of("return ${lastGenVal.name}"), varTypeNullable)
            val companionObject = TypeSpec.companionObjectBuilder()
                .addProperty(lastGenVal)
                .addFunction(genValGetter)
                .build()
            typeBuilder.addType(companionObject)


            val genFunBuilder = FunSpec.builder("generate")
                .addModifiers(KModifier.OVERRIDE, KModifier.PUBLIC)
                .addParameter("random", SourceOfRandomness::class)
                .addParameter("ignore", GenerationStatus::class)
                .returns(varType)


            val isRandomisedNotGenerated = (getRandomizedTypes().map { it.qualifiedName }.find { it == varType.canonicalName } != null)
            if (isRandomisedNotGenerated) {

                val resRandomizer = randomizers.find {
                    val className = it.second?.type?.toString()
                    val simpleClassName = className?.split(".")?.last()
                    val generatedType = classToRandomizerClass.entries.find { it.value == simpleClassName }?.key?.split(".")?.last()
                    varType.simpleName == generatedType

                }
                genFunBuilder
                    .addCode("val filler = random.nextBoolean()\n")
                    .addCode("return %N.randomValue", resRandomizer?.second?.name)
            }
            else {
                val erType = ClassName("org.jeasy.random", "EasyRandom")
                genFunBuilder
                    .addCode("val filler = random.nextBoolean()\n")
                    .addCode("val generatedValue = %T(erConfig).nextObject(${varType.simpleName}::class.java)\n", erType)
                    .addCode("lastGenVal = generatedValue\n")
                    .addCode("return generatedValue\n")
            }
            val genFun = genFunBuilder.build()
            typeBuilder.addFunction(genFun)

            val retFunBuilder = FunSpec.builder("getValue")
                .addModifiers(KModifier.PUBLIC)
                .returns(varType)

            if (isRandomisedNotGenerated) {
                val resRandomizer = randomizers.find {
                    val className = it.second?.type?.toString()
                    val simpleClassName = className?.split(".")?.last()
                    val generatedType = classToRandomizerClass.entries.find { it.value == simpleClassName }?.key?.split(".")?.last()
                    varType.simpleName == generatedType

                }
                retFunBuilder
                    .addCode("return %N.randomValue", resRandomizer?.second?.name)
            }
            else {
                val erType = ClassName("org.jeasy.random", "EasyRandom")
                retFunBuilder
                    .addCode("val generatedValue = %T(erConfig).nextObject(${varType.simpleName}::class.java)\n", erType)
                    .addCode("lastGenVal = generatedValue\n")
                    .addCode("return generatedValue\n")
            }
            val retFun = retFunBuilder.build()
            typeBuilder.addFunction(retFun)


            builder.addType(typeBuilder.build())
            return builder.build()
        }

        fun makeGeneratorName(nameParts: List<String>): String {
            return nameParts.joinToString("_", postfix = "_Generator")
        }

    }
}