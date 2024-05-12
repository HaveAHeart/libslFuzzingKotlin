package com.spbpu.genFuzzing.generators

import com.pholser.junit.quickcheck.generator.GenerationStatus
import com.pholser.junit.quickcheck.generator.Generator
import com.pholser.junit.quickcheck.random.SourceOfRandomness
import com.spbpu.URLHolder
import com.spbpu.randomizers.StringLibSLRandomizer
import java.lang.Class
import kotlin.Int
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters

public class URLHolder_getURL_Generator(
  type: Class<URLHolder>,
) : Generator<URLHolder>(type) {
  private val urlRandomizer: StringLibSLRandomizer = StringLibSLRandomizer(10, 25,
      """(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?\/[a-zA-Z0-9]{2,}|((https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?)|(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})?""")

  private val lenRandomizer: () -> Int = { urlRandomizer.getLastGenValue().length }

  private val erConfig: EasyRandomParameters = EasyRandomParameters().randomize({ field ->
      field.name == "url" }, urlRandomizer).randomize({ field -> field.name == "len" },
      lenRandomizer)

  public override fun generate(random: SourceOfRandomness, ignore: GenerationStatus): URLHolder {
    val filler = random.nextBoolean()
    val generatedValue = EasyRandom(erConfig).nextObject(URLHolder::class.java)
    lastGenVal = generatedValue
    return generatedValue
  }

  public companion object {
    private var lastGenVal: URLHolder? = null

    public fun getLastGenVal(): URLHolder? = lastGenVal
  }
}
