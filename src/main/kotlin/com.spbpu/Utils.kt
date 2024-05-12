package com.spbpu

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import org.jetbrains.research.libsl.nodes.*
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.references.TypeReference
import org.jetbrains.research.libsl.type.StructuredType
import org.jetbrains.research.libsl.type.Type
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.reflect.KClass

class Utils {
    companion object {
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
            println("resolved klazz is $klazz")
            var fullName = ""
            if (klazz == null) {
                val type = types.find { it.name == typeReference!!.name } as StructuredType
                fullName = type.isTypeIdentifier!!
            }
            else {
                fullName = klazz.qualifiedName!!
            }
            println("fullname $fullName")

            println("split ${fullName.split(".")}")
            val typeName = fullName.split(".").last()
            val packageName = fullName.split(".").dropLast(1).joinToString(".")
            println("$packageName $typeName")
            val name = ClassName(packageName, typeName)

            println("name ${name.canonicalName}")
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

        fun cleanGenFolder() {
            val tmpPath = Path("src/test/kotlin/com/spbpu/genFuzzing")
            tmpPath.toFile().deleteRecursively()
            tmpPath.createDirectory()
        }
    }
}