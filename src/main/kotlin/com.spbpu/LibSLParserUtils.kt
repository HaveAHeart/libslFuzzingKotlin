package com.spbpu

import com.squareup.kotlinpoet.ClassName
import org.jetbrains.research.libsl.LibSL
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.Library
import java.io.File
import kotlin.math.max
import kotlin.reflect.KClass

class LibSLParserUtils {
    companion object {
        val classToRandomizerClass = mapOf(
            Pair(Char::class.qualifiedName, "CharLibSLRandomizer"),
            Pair(String::class.qualifiedName, "StringLibSLRandomizer"),
            Pair(Byte::class.qualifiedName, "ByteLibSLRandomizer"),
            Pair(Short::class.qualifiedName, "ShortLibSLRandomizer"),
            Pair(Int::class.qualifiedName, "IntegerLibSLRandomizer"),
            Pair(Long::class.qualifiedName, "LongLibSLRandomizer"),
            Pair(Float::class.qualifiedName, "FloatLibSLRandomizer"),
            Pair(Double::class.qualifiedName, "DoubleLibSLRandomizer"),
            Pair(Boolean::class.qualifiedName, "BooleanLibSLRandomizer"),
        )

        fun getLibrary(basePath: String, contentPath: String): Library {
            return LibSL(basePath).loadFromFile(File(contentPath))
        }

        fun getFieldClassFromAnnotation(annotation: Annotation): String {
            val args = annotation.argumentDescriptors
            val className = Utils.getStringFromExpression(args.find { it.name == "class" }?.initialValue, "")
            return className
        }

        fun getParamsFromAnnotation(annotation: Annotation, varType: ClassName): List<String> {
            val args = annotation.argumentDescriptors
            return when (varType.canonicalName) {
                Char::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val availibleChars = Utils.getStringFromExpression(args.find { it.name == "availibleChars" }?.initialValue, "_")

                    listOf(name, availibleChars)
                }
                 String::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minLength = Utils.getIntFromExpression(args.find { it.name == "minLength" }?.initialValue, 1)
                    val maxLength = Utils.getIntFromExpression(args.find { it.name == "maxLength" }?.initialValue, 50)
                    val regexp = Utils.getStringFromExpression(args.find { it.name == "regexp" }?.initialValue, ".*")

                    listOf(name, minLength.toString(), maxLength.toString(), "\"\"\"$regexp\"\"\"")
                }
                Byte::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Byte.MIN_VALUE.toInt())
                    val maxValue = Utils.getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Byte.MAX_VALUE.toInt())

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Short::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Short.MIN_VALUE.toInt())
                    val maxValue = Utils.getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Short.MAX_VALUE.toInt())

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Int::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Int.MIN_VALUE)
                    val maxValue = Utils.getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Int.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Long::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getLongFromExpression(args.find { it.name == "minValue" }?.initialValue, Long.MIN_VALUE)
                    val maxValue = Utils.getLongFromExpression(args.find { it.name == "maxValue" }?.initialValue, Long.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Float::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getFloatFromExpression(args.find { it.name == "minValue" }?.initialValue, Float.MIN_VALUE)
                    val maxValue = Utils.getFloatFromExpression(args.find { it.name == "maxValue" }?.initialValue, Float.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Double::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = Utils.getDoubleFromExpression(args.find { it.name == "minValue" }?.initialValue, Double.MIN_VALUE)
                    val maxValue = Utils.getDoubleFromExpression(args.find { it.name == "maxValue" }?.initialValue, Double.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Boolean::class.qualifiedName -> {
                    val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val defaultValue = Utils.getBooleanFromExpression(args.find { it.name == "defaultValue" }?.initialValue)

                    listOf(name, defaultValue.toString())
                }
                else -> { return listOf() }
            }
        }

        fun getDependableParamsFromAnnotation(annotation: Annotation): List<String> {
            val args = annotation.argumentDescriptors
            println("for params from annotation args ${args}")
            val name = Utils.getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
            val dependsOn = Utils.getStringFromExpression(args.find { it.name == "dependsOn" }?.initialValue, "it")
            val dependsWith = Utils.getStringFromExpression(args.find { it.name == "dependsWith" }?.initialValue, "it")

            return listOf(name, dependsOn, dependsWith)
        }

        fun isDependableAnnotation(annotation: Annotation): Boolean {
            val args = annotation.argumentDescriptors
            val dependsOn = Utils.getStringFromExpression(args.find { it.name == "dependsOn" }?.initialValue, "")

            return dependsOn.isNotEmpty()
        }
    }
}