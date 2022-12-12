package de.tek.adventofcode.y2022.day06

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class StreamSnippetTest : StringSpec({
    "findStartOfPacketMarker" {
        forAll(
            row("bvwbjplbgvbhsrlpgdmjqwftvncz", 5),
            row("nppdvjthqldpwncqszvftbrmjlhg", 6),
            row("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10),
            row("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11)
        ) { input, expectedResult ->
            val inputAsStream = input.byteInputStream().bufferedReader()
            DataStreamAnalyzer(4).findStartMarker(inputAsStream) shouldBe expectedResult
        }
    }

    "findStartOfMessageMarker" {
        forAll(
            row("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 19),
            row("bvwbjplbgvbhsrlpgdmjqwftvncz", 23),
            row("nppdvjthqldpwncqszvftbrmjlhg", 23),
            row("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 29),
            row("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 26)
        ) { input, expectedResult ->
            val inputAsStream = input.byteInputStream().bufferedReader()
            DataStreamAnalyzer(14).findStartMarker(inputAsStream) shouldBe expectedResult
        }
    }
})