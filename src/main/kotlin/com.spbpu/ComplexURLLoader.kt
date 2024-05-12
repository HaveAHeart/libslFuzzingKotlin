package com.spbpu

class ComplexURLLoader(initLoader: URLLoader) {
    private var loader: URLLoader
    init {
        loader = initLoader
    }
    fun getURLLoader(): URLLoader {
        return loader
    }
    fun getURL(): String {
        return loader.getURL()
    }
}