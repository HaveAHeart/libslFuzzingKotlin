package com.spbpu.genFuzzing.fuzzTests

import com.pholser.junit.quickcheck.From
import com.spbpu.URLHolder
import com.spbpu.genFuzzing.generators.URLHolder_getURL_Generator
import edu.berkeley.cs.jqf.fuzz.Fuzz
import edu.berkeley.cs.jqf.fuzz.JQF
import org.junit.Assert
import org.junit.runner.RunWith

@RunWith(JQF::class)
public class URLHolder_getURL_FuzzTest {
  @Fuzz
  public fun isURLPresent(@From(URLHolder_getURL_Generator::class) genResult: URLHolder) {
    Assert.assertTrue(genResult.getURL().length > 0)
  }

  @Fuzz
  public fun isLenPresent(@From(URLHolder_getURL_Generator::class) genResult: URLHolder) {
    Assert.assertTrue(genResult.getLen() > 0)
  }
}
