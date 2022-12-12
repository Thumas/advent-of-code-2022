package de.tek.adventofcode.y2022.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import kotlin.reflect.KClass

fun <T : Any> readInputLines(classToTest: KClass<T>): List<String> {
    val resourcePath = getPackageName(classToTest)
    return getInputStream(resourcePath)?.bufferedReader()?.readLines()
        ?: throw IOException("Could not read data from file $resourcePath.")
}

fun <T : Any> getInputStream(classToTest: KClass<T>): BufferedReader {
    val resourcePath = getPackageName(classToTest)
    return getInputStream(resourcePath)?.bufferedReader()
        ?: throw IOException("Could not read data from file $resourcePath.")
}

private fun <T : Any> getPackageName(classToTest: KClass<T>): String = classToTest.java.`package`.name

private fun getInputStream(packageName: String): InputStream? {
    val resourcePath = '/' + packageName.replace('.', '/') + "/input.txt"
    return object {}.javaClass.getResourceAsStream(resourcePath)
}