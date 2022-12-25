package de.tek.adventofcode.y2022.day15

import de.tek.adventofcode.y2022.util.math.Point
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions

class BeaconExclusionZoneTest : StringSpec({
    "Given a line, parsePointsFromLine returns the expected result." {
        forAll(
            row("Sensor at x=2, y=18: closest beacon is at x=-2, y=15", Pair(Point(2, 18), Point(-2, 15))),
            row("Sensor at x=9, y=16: closest beacon is at x=10, y=16", Pair(Point(9, 16), Point(10, 16))),
            row("Sensor at x=13, y=2: closest beacon is at x=15, y=3", Pair(Point(13, 2), Point(15, 3))),
            row("Sensor at x=12, y=14: closest beacon is at x=10, y=16", Pair(Point(12, 14), Point(10, 16))),
        ) { line, expectedParseResult ->
            parsePointsFromLine(line) shouldBe expectedParseResult
        }
    }

    "Given example, the points covered by the sensors at height 10 are as expected." {
        val input = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3""".split("\n")

        val sensorBeaconPairs = input.map(::parsePointsFromLine)

        Assertions.assertThat(linePointsInSensorRadius(sensorBeaconPairs, 10).toSet())
            .containsExactlyInAnyOrderElementsOf((-2..24).map { x -> Point(x, 10) })
    }
})