package com.spbpu

class URLLoader (initURL: String) {
    private var url: String

    init {
        url = initURL
    }
    fun getURL(): String {
        return url;
    }
}