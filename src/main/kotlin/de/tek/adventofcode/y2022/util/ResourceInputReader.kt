package de.tek.adventofcode.y2022.util

import java.io.IOException
import kotlin.reflect.KClass

fun <T : Any> readInputLines(classToTest: KClass<T>): List<String> {
    val packageName = classToTest.java.`package`.name
    val resourcePath = '/' + packageName.replace('.', '/') + "/input.txt"
    return object {}.javaClass.getResourceAsStream(resourcePath)?.bufferedReader()?.readLines()
        ?: throw IOException("Could not read data from file $resourcePath.")
}