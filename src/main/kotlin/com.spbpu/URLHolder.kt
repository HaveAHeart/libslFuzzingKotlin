package com.spbpu

class URLHolder (initURL: String, initLen: Int) {
    private var url: String
    private var len: Int

    init {
        url = initURL
        len = initLen
    }
    fun getURL(): String {
        return url
    }
    fun getLen(): Int {
        return len
    }
}