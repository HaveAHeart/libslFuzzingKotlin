package com.spbpu

import com.pholser.junit.quickcheck.From
import edu.berkeley.cs.jqf.fuzz.Fuzz
import edu.berkeley.cs.jqf.fuzz.JQF
import org.junit.runner.RunWith


@RunWith(JQF::class)
class FuzzingTest {
    @Fuzz
    fun testURLHolder(@From(URLHolderGen::class) holder: URLHolder) {
        println("current url is ${holder.getURL()}")
        assert(holder.getURL().isNotEmpty())
    }


    @Fuzz
    fun testURLLoaderWithGenerator (
        @From(URLLoaderGen::class) loader: URLLoader,
        @From(LengthGen::class) length: Int
        ) {

        //assumeThat((loader.getURL().length), equalTo(length))

        val urlRegexp = """(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?\/[a-zA-Z0-9]{2,}|((https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z]{2,}(\.[a-zA-Z]{2,})(\.[a-zA-Z]{2,})?)|(https:\/\/www\.|http:\/\/www\.|https:\/\/|http:\/\/)?[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}\.[a-zA-Z0-9]{2,}(\.[a-zA-Z0-9]{2,})?"""
        val regexp = urlRegexp.toRegex()
        val url = loader.getURL()
        println("url with length $length = ${url.length}: $url")
        assert(regexp.containsMatchIn(url))
    }

    @Fuzz
    fun alwaysPasses(string: String) {
        assert(string.isNotEmpty())
    }
}

