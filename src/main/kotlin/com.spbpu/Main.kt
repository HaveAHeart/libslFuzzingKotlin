@file:JvmName("AgentX")

package com.spbpu

import com.spbpu.FuzzTestsGenerator.Companion.makeFuzzTestsForLibrary
import com.spbpu.GeneratorGenerator.Companion.makeGeneratorsForLibrary
import com.spbpu.Utils.Companion.cleanGenFolder
import javassist.ClassPool
import javassist.LoaderClassPath
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

fun premain(arguments: String?, instrumentation: Instrumentation) {
    println("AgentX 'premain' starts here.")

    val libPath = "C:\\Users\\PSPOD\\IdeaProjects\\libslFuzzingKotlin\\libslSpecs\\spec"
    val specPath = "C:\\Users\\PSPOD\\IdeaProjects\\libslFuzzingKotlin\\libslSpecs\\testAnnotations\\testAnnotations.lsl"
    val library = LibSLParserUtils.getLibrary(libPath, specPath)

    val generators = makeGeneratorsForLibrary(library)
    val basePath = Path("gen/")
    if (!basePath.exists()) { basePath.createDirectories() }

    generators.forEach { gen ->
        val fullName = gen.packageName + "." + gen.name
        val genClass = Class.forName(fullName)

        val splitName = gen.name.split("_")
        val methodName = splitName[1]
        val genType = gen.typeSpecs.first().primaryConstructor?.parameters?.first()?.type.toString()
        val resGenTypeName = genType.split(regex = Regex("[<>]"))[1]

        val targetClass = Class.forName(resGenTypeName)
        val targetCL = targetClass.classLoader
        val transformer = CustomTransformer(targetClass.canonicalName, targetCL, methodName, genClass)

        instrumentation.addTransformer(transformer, true)
        instrumentation.retransformClasses(targetClass)
    }
}

fun main(args: Array<String>) {

    val libPath = "C:\\Users\\PSPOD\\IdeaProjects\\libslFuzzingKotlin\\libslSpecs\\spec"
    val specPath = "C:\\Users\\PSPOD\\IdeaProjects\\libslFuzzingKotlin\\libslSpecs\\testAnnotations\\testAnnotations.lsl"
    val library = LibSLParserUtils.getLibrary(libPath, specPath)

    val generators = makeGeneratorsForLibrary(library)
    val tests = makeFuzzTestsForLibrary(library)

    val basePath = Path("src/main/kotlin")
    if (!basePath.exists()) { basePath.createDirectories() }
    cleanGenFolder(basePath.toString())
    generators.forEach { it.writeTo(basePath.toFile()) }
    tests.forEach { it.writeTo(basePath.toFile()) }
}

//mvn jqf:fuzz -Dclass=com.spbpu.FuzzingTest -Dmethod=testURLLoaderWithGenerator -Dtime=10s

class CustomTransformer(
        val targetClassName: String,
        val classLoader: ClassLoader,
        val targetMethodName: String,
        val genClass: Class<*>
    ): ClassFileTransformer {
    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain?,
        classfileBuffer: ByteArray?
    ): ByteArray {
        if (classfileBuffer == null) { return ByteArray(0) }

        if (classBeingRedefined?.canonicalName != this.targetClassName) {
            return classfileBuffer
        }
        try {
            val cc = ClassPool.getDefault()
            cc.appendClassPath(LoaderClassPath(classLoader))
            cc.importPackage(genClass.canonicalName)
            cc.importPackage(classBeingRedefined.canonicalName)

            val cl = cc.getOrNull(classBeingRedefined.canonicalName)
            val genCtClass = cc.getOrNull(genClass.canonicalName)
            val m = cl.getDeclaredMethod(targetMethodName)

            m.addLocalVariable("generator", genCtClass)
            val builder = StringBuilder()
            builder.appendLine("{")
            builder.appendLine("System.out.println(\"Java Agent premain insertion starts here. \");")
            builder.appendLine("System.out.println(\"changing ${classBeingRedefined.canonicalName}.${targetMethodName}() result using ${genClass.canonicalName}.getValue() ...\");")
            builder.appendLine("generator = new ${genClass.canonicalName}(${classBeingRedefined.canonicalName}.class);")
            builder.appendLine("return generator.getValue();")
            builder.appendLine("}")

            m.insertBefore(builder.toString())

            val byteCode = cl.toBytecode()
            cl.writeFile()
            cl.detach()

            return byteCode
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        return byteArrayOf()
    }
}
