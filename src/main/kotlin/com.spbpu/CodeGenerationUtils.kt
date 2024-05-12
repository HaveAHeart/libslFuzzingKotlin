package com.spbpu

import com.squareup.kotlinpoet.*
import org.cornutum.regexpgen.RandomGen
import org.cornutum.regexpgen.RegExpGen
import org.jeasy.random.api.Randomizer
import org.jetbrains.research.libsl.nodes.Annotation
import org.jetbrains.research.libsl.nodes.Automaton
import kotlin.reflect.KClass

class CodeGenerationUtils {
    companion object {
        fun makeStringRandomizer(typeName: String): FileSpec {
            val builder = FileSpec.builder("com.spbpu.randomizers", "${typeName}LibSLRandomizer")
            val randomizerType = makeStringRandomizerClass(typeName)
            builder.addType(randomizerType)

            val imports = listOf(
                "com.spbpu.Utils",
                "org.cornutum.regexpgen.RandomGen",
                "org.cornutum.regexpgen.RegExpGen",
                "org.cornutum.regexpgen.js.Provider",
                "org.cornutum.regexpgen.random.RandomBoundsGen",
                "org.jeasy.random.api.Randomizer",
                "org.jetbrains.research.libsl.nodes.Annotation",
            )
            builder.addImport("", imports)

            return builder.build()
        }

        fun makeType(typeName: String): TypeSpec {
            return TypeSpec.classBuilder(typeName).build()
        }



        fun makeStringRandomizerClass(typeName: String): TypeSpec {
            val name = "${typeName}LibSLRandomizer"
            val builder = TypeSpec.classBuilder(name).addSuperinterface(Randomizer::class)

            val constructor = makeConstructor(listOf(Pair("initAnnotation", Annotation::class)))
            builder.primaryConstructor(constructor)

            val props = listOf(
                makeProperty(KModifier.PRIVATE, false, "annotation", Annotation::class, "initAnnotation"),
                makeProperty(KModifier.PRIVATE, true, "minLength", Int::class, "0"),
                makeProperty(KModifier.PRIVATE, true, "maxLength", Int::class, "50"),
                makeProperty(KModifier.PRIVATE, true, "regexp", String::class, ".*"),
                makeProperty(KModifier.PRIVATE, false, "random", RandomGen::class, "RandomBoundsGen()"),
                makeProperty(KModifier.PROTECTED, false, "generator", RegExpGen::class),
            )
            builder.addProperties(props)

            val statements = listOf(
                Pair("val args = annotation?.argumentDescriptors", listOf<String>()),
                Pair("minLength = Utils.getIntFromExpression(args?.find { it.name == \"minLength\" }?.initialValue, 0)", listOf()),
                Pair("maxLength = Utils.getIntFromExpression(args?.find { it.name == \"maxLength\" }?.initialValue, 50)", listOf()),
                Pair("regexp = Utils.getStringFromExpression(args?.find { it.name == \"regexp\" }?.initialValue, \"\")", listOf()),
                Pair("generator = Provider.forEcmaScript().matchingExact(regexp)", listOf()),
                Pair("println(\"StringGen initialized. Length: \$minLength : \$maxLength\")", listOf()),
            )
            val initSection = makeCodeBlock(statements)
            builder.addInitializerBlock(initSection)


            val funCode = makeCodeBlock(listOf(Pair("return generator.generate(random, minLength, maxLength)", listOf())))
            val getVal = makeFunction(true, "getRandomValue", funCode, String::class)
            builder.addFunction(getVal)

            return builder.build()
        }

        fun makeCodeBlock(statements: List<Pair<String, List<String>>>): CodeBlock {
            val builder = CodeBlock.builder()
            statements.forEach { st -> builder.addStatement(st.first, st.second) }

            return builder.build()
        }

        fun makeConstructor(params: List<Pair<String, KClass<*>>>): FunSpec {
            val builder = FunSpec.constructorBuilder()

            params.forEach { param -> builder.addParameter(param.first, param.second) }

            return builder.build()
        }

        fun makeParameter(name: String, clazz: KClass<*>): FunSpec {
            return FunSpec.constructorBuilder()
                .addParameter(name, clazz)
                .build()
        }

        fun makeProperty(mod: KModifier, isMutable: Boolean, name: String, clazz: KClass<*>, initVal: String): PropertySpec {
            return PropertySpec.builder(name, clazz)
                .initializer(initVal)
                .mutable(isMutable)
                .addModifiers(mod)
                .build()
        }

        fun makeProperty(mod: KModifier, isMutable: Boolean, name: String, clazz: KClass<*>): PropertySpec {
            return PropertySpec.builder(name, clazz)
                .mutable(isMutable)
                .addModifiers(mod)
                .build()
        }

        fun makeProperty(mod: KModifier, isMutable: Boolean, name: String, clazz: TypeName): PropertySpec {
            return PropertySpec.builder(name, clazz)
                .mutable(isMutable)
                .addModifiers(mod)
                .build()
        }

        fun makeProperty(mod: KModifier, isMutable: Boolean, name: String, clazz: TypeName, initVal: String): PropertySpec {
            return PropertySpec.builder(name, clazz)
                .initializer(initVal)
                .mutable(isMutable)
                .addModifiers(mod)
                .build()
        }

        fun makeFunction(isOverriding: Boolean, name: String, code: CodeBlock, retType: KClass<*>): FunSpec {
            val builder = FunSpec.builder(name)
            if (isOverriding) { builder.addModifiers(KModifier.OVERRIDE) }
            builder.addCode(code)
            builder.returns(retType)

            return builder.build()
        }

        fun makeFunction(isOverriding: Boolean, name: String, code: CodeBlock, retType: TypeName): FunSpec {
            val builder = FunSpec.builder(name)
            if (isOverriding) { builder.addModifiers(KModifier.OVERRIDE) }
            builder.addCode(code)
            builder.returns(retType)

            return builder.build()
        }
    }

}