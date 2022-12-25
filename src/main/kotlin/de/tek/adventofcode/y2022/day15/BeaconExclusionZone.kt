package de.tek.adventofcode.y2022.day15

import de.tek.adventofcode.y2022.util.math.OneNorm
import de.tek.adventofcode.y2022.util.math.Point
import de.tek.adventofcode.y2022.util.readInputLines
import kotlin.math.abs

class Sensor

fun main() {
    val input = readInputLines(Sensor::class)

    fun part1(input: List<String>): Int {
        val lineHeight = 2000000

        val sensorBeaconPairs = input.map(::parsePointsFromLine)

        val linePointsInSensorRadius = linePointsInSensorRadius(sensorBeaconPairs, lineHeight).toSet()
        val beaconsOnLine = sensorBeaconPairs.map { (_, beacon) -> beacon }.filter { it.y == lineHeight }.toSet()

        return (linePointsInSensorRadius - beaconsOnLine).size
    }

    println("In the row where y=2000000, ${part1(input)} positions cannot contain a beacon.")
}

fun linePointsInSensorRadius(sensorBeaconPairs: List<Pair<Point,Point>>, lineHeight: Int): Sequence<Point> =
    with(OneNorm) {
        sensorBeaconPairs
            .asSequence()
            .map { (sensor, beacon) -> sensor to (sensor distanceTo beacon) }
            .filter { (sensor, radius) -> abs(sensor.y - lineHeight) <= radius }
            .flatMap { (sensor, radius) -> linePointsInSensorRadius(radius, sensor, lineHeight) }
    }

fun linePointsInSensorRadius(radius: Int, sensor: Point, lineHeight: Int): List<Point> {
    val maxDeltaX = radius - abs(sensor.y - lineHeight)
    println("Sensor is at $sensor, radius is $radius, so the interval at y=$lineHeight is [${sensor.x-maxDeltaX},${sensor.x + maxDeltaX}].")
    return (-maxDeltaX..maxDeltaX).map { deltaX -> Point(sensor.x + deltaX, lineHeight) }
}

private val sensorRegex = Regex("""Sensor at x=([^,]+?), y=([^:]+?): closest beacon is at x=([^,]+?), y=([^,]+)""")

fun parsePointsFromLine(line: String): Pair<Point, Point> {
    val sensorRegexMatches = sensorRegex.matchEntire(line)
    if (sensorRegexMatches != null) {
        val (sensorX, sensorY, beaconX, beaconY) = sensorRegexMatches.destructured

        return Pair(Point(sensorX.toInt(), sensorY.toInt()), Point(beaconX.toInt(), beaconY.toInt()))
    }
    throw IllegalArgumentException("Input line does not match the expected format: '$line' does not match '$sensorRegex'.")
}