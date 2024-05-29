package com.spbpu

import com.squareup.kotlinpoet.ClassName
import org.jetbrains.research.libsl.LibSL
import org.jetbrains.research.libsl.nodes.*
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.references.TypeReference
import org.jetbrains.research.libsl.type.StructuredType
import org.jetbrains.research.libsl.type.Type
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
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
            val className = getStringFromExpression(args.find { it.name == "class" }?.initialValue, "")
            return className
        }

        fun getParamsFromAnnotation(annotation: Annotation, varType: ClassName): List<String> {
            val args = annotation.argumentDescriptors
            return when (varType.canonicalName) {
                Char::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val availibleChars = getStringFromExpression(args.find { it.name == "availibleChars" }?.initialValue, "_")

                    listOf(name, availibleChars)
                }
                 String::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minLength = getIntFromExpression(args.find { it.name == "minLength" }?.initialValue, 1)
                    val maxLength = getIntFromExpression(args.find { it.name == "maxLength" }?.initialValue, 50)
                    val regexp = getStringFromExpression(args.find { it.name == "regexp" }?.initialValue, ".*")

                    listOf(name, minLength.toString(), maxLength.toString(), "\"\"\"$regexp\"\"\"")
                }
                Byte::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Byte.MIN_VALUE.toInt())
                    val maxValue = getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Byte.MAX_VALUE.toInt())

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Short::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Short.MIN_VALUE.toInt())
                    val maxValue = getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Short.MAX_VALUE.toInt())

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Int::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getIntFromExpression(args.find { it.name == "minValue" }?.initialValue, Int.MIN_VALUE)
                    val maxValue = getIntFromExpression(args.find { it.name == "maxValue" }?.initialValue, Int.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Long::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getLongFromExpression(args.find { it.name == "minValue" }?.initialValue, Long.MIN_VALUE)
                    val maxValue = getLongFromExpression(args.find { it.name == "maxValue" }?.initialValue, Long.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Float::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getFloatFromExpression(args.find { it.name == "minValue" }?.initialValue, Float.MIN_VALUE)
                    val maxValue = getFloatFromExpression(args.find { it.name == "maxValue" }?.initialValue, Float.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Double::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val minValue = getDoubleFromExpression(args.find { it.name == "minValue" }?.initialValue, Double.MIN_VALUE)
                    val maxValue = getDoubleFromExpression(args.find { it.name == "maxValue" }?.initialValue, Double.MAX_VALUE)

                    listOf(name, minValue.toString(), maxValue.toString())
                }
                Boolean::class.qualifiedName -> {
                    val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
                    val defaultValue = getBooleanFromExpression(args.find { it.name == "defaultValue" }?.initialValue)

                    listOf(name, defaultValue.toString())
                }
                else -> { return listOf() }
            }
        }

        fun getDependableParamsFromAnnotation(annotation: Annotation): List<String> {
            val args = annotation.argumentDescriptors
            val name = getStringFromExpression(args.find { it.name == "name" }?.initialValue, "result")
            val dependsOn = getStringFromExpression(args.find { it.name == "dependsOn" }?.initialValue, "it")
            val dependsWith = getStringFromExpression(args.find { it.name == "dependsWith" }?.initialValue, "it")

            return listOf(name, dependsOn, dependsWith)
        }

        fun isDependableAnnotation(annotation: Annotation): Boolean {
            val args = annotation.argumentDescriptors
            val dependsOn = getStringFromExpression(args.find { it.name == "dependsOn" }?.initialValue, "")

            return dependsOn.isNotEmpty()
        }

        private val classResolveMap = mapOf<String, KClass<*>>(
            "char" to Char::class,
            "string" to String::class,
            "int8" to Byte::class,
            "int16" to Short::class,
            "int32" to Int::class,
            "int64" to Long::class,
            "float32" to Float::class,
            "float64" to Double::class,
            "bool" to Boolean::class,

            )

        fun resolveLibSLType(typeReference: TypeReference?): KClass<*>? {
            if (typeReference == null) { return Void::class }
            val typeName = typeReference.name
            val clazz = classResolveMap[typeName]

            return clazz
        }

        fun resolveLibSLTypeToClassName(types: List<Type>, typeReference: TypeReference?): ClassName {
            val klazz = resolveLibSLType(typeReference)
            var fullName = ""
            if (klazz == null) {
                val type = types.find { it.name == typeReference!!.name } as StructuredType
                fullName = type.isTypeIdentifier!!
            }
            else {
                fullName = klazz.qualifiedName!!
            }

            val typeName = fullName.split(".").last()
            val packageName = fullName.split(".").dropLast(1).joinToString(".")
            val name = ClassName(packageName, typeName)

            return name
        }

        fun getRandomizedTypes(): List<KClass<*>> {
            return classResolveMap.values.toList()
        }

        fun getStringFromExpression(expression: Expression?, default: String): String {
            val castExpression = expression as? StringLiteral
            return castExpression?.value ?: default
        }
        fun getIntFromExpression(expression: Expression?, default: Int): Int {
            val castExpression = expression as? IntegerLiteral
            return castExpression?.value?.toInt() ?: default
        }
        fun getLongFromExpression(expression: Expression?, default: Long): Long {
            val castExpression = expression as? IntegerLiteral
            return castExpression?.value?.toLong() ?: default
        }
        fun getFloatFromExpression(expression: Expression?, default: Float): Float {
            val castExpression = expression as? FloatLiteral
            return castExpression?.value?.toFloat() ?: default
        }
        fun getDoubleFromExpression(expression: Expression?, default: Double): Double {
            val castExpression = expression as? FloatLiteral
            return castExpression?.value?.toDouble() ?: default
        }
        fun getBooleanFromExpression(expression: Expression?): Boolean? {
            val castExpression = expression as? BoolLiteral
            return castExpression?.value
        }

        fun cleanGenFolder(basePath: String) {
            val tmpPath = Path("$basePath/com/spbpu/genFuzzing")
            if (tmpPath.exists()) { tmpPath.toFile().deleteRecursively() }
            tmpPath.createDirectories()
        }
    }
}