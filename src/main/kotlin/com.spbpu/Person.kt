package com.spbpu

/*class Person(name: String, height: Float, isBald: Boolean) {
    var name: String
    var height: Float
    var isBald: Boolean

    init {
        this.name = name
        this.height = height
        this.isBald = isBald
    }
}*/
class Person(val name: String, val height: Float, val isBald: Boolean) {
    fun getPerson(): Person {
        return this
    }
}