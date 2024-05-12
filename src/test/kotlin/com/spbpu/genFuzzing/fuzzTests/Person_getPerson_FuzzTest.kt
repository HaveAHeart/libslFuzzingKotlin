package com.spbpu.genFuzzing.fuzzTests

import com.pholser.junit.quickcheck.From
import com.spbpu.Person
import com.spbpu.genFuzzing.generators.Person_getPerson_Generator
import edu.berkeley.cs.jqf.fuzz.Fuzz
import edu.berkeley.cs.jqf.fuzz.JQF
import org.junit.Assert
import org.junit.runner.RunWith

@RunWith(JQF::class)
public class Person_getPerson_FuzzTest {
  @Fuzz
  public fun isPersonPresent(@From(Person_getPerson_Generator::class) genResult: Person) {
    Assert.assertTrue(genResult != null)
  }
}
