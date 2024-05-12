package com.spbpu.genFuzzing.generators

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.Person
import com.spbpu.randomizers.FloatLibSLRandomizer
import com.spbpu.randomizers.StringLibSLRandomizer
import java.lang.Class
import kotlin.Boolean
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters

public class Person_getPerson_Generator(
  type: Class<Person>,
) : Generator<Person>(type) {
  private val heightRandomizer: FloatLibSLRandomizer = FloatLibSLRandomizer(120.1F, 199.9F)

  private val nameRandomizer: StringLibSLRandomizer = StringLibSLRandomizer(10, 25,
      """[A-Z][a-z]+""")

  private val isBaldRandomizer: () -> Boolean = { heightRandomizer.getLastGenValue().toInt() > 150 }

  private val erConfig: EasyRandomParameters = EasyRandomParameters().randomize({ field ->
      field.name == "height" }, heightRandomizer).randomize({ field -> field.name == "name" },
      nameRandomizer).randomize({ field -> field.name == "isBald" }, isBaldRandomizer)

  public override fun generate(random: SourceOfRandomness, ignore: GenerationStatus): Person {
    val filler = random.nextBoolean()
    val generatedValue = EasyRandom(erConfig).nextObject(Person::class.java)
    lastGenVal = generatedValue
    return generatedValue
  }

  public companion object {
    private var lastGenVal: Person? = null

    public fun getLastGenVal(): Person? = lastGenVal
  }
}
