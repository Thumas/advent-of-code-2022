package de.tek.adventofcode.y2022.day06

import de.tek.adventofcode.y2022.util.getInputStream
import java.io.BufferedReader

class StreamSnippet(private val markerLength: Int) {
    private val snippet = ArrayDeque<Char>(markerLength)

    fun add(c: Char): StreamSnippet {
        while (snippet.size >= markerLength) {
            snippet.removeFirst()
        }
        snippet.add(c)

        return this
    }

    fun isValidMarker() = snippet.toSet().size == markerLength
}

class DataStreamAnalyzer(private val markerLength: Int) {
    fun findStartMarker(stream: BufferedReader): Int {
        val snippet = StreamSnippet(markerLength)
        val count = wrapAsCharSequence(stream).map(snippet::add).takeWhile { !snippet.isValidMarker() }
            .count()
        if (snippet.isValidMarker()) {
            return count + 1
        } else {
            throw NoSuchElementException("No packet marker could be found.")
        }
    }

    private fun wrapAsCharSequence(stream: BufferedReader) =
        generateSequence {
            val nextInt = stream.read()
            if (nextInt != -1) {
                nextInt.toChar()
            } else {
                null
            }
        }
}


fun main() {
    fun part1(): Int {
        val inputStream = getInputStream(DataStreamAnalyzer::class)
        return DataStreamAnalyzer(4).findStartMarker(inputStream)
    }

    fun part2(): Int {
        val inputStream = getInputStream(DataStreamAnalyzer::class)
        return DataStreamAnalyzer(14).findStartMarker(inputStream)
    }

    println("${part1()} characters had to be read before the first start-of-packet marker was detected.")
    println("${part2()} characters had to be read before the first start-of-message marker was detected.")
}