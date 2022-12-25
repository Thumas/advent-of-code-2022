package de.tek.adventofcode.y2022.day15

import de.tek.adventofcode.y2022.util.math.Norm
import de.tek.adventofcode.y2022.util.math.OneNorm
import de.tek.adventofcode.y2022.util.math.Point
import de.tek.adventofcode.y2022.util.readInputLines
import kotlin.math.abs

class SensorAndBeacon(val sensor: Point, private val beacon: Point) {
    fun getSensorRadius(): Int = with(OneNorm) { sensor distanceTo beacon }

    operator fun component1(): Point = sensor
    operator fun component2(): Point = beacon
}

fun main() {
    val input = readInputLines(SensorAndBeacon::class)

    println("In the row where y=2000000, ${part1(input)} positions cannot contain a beacon.")

    println("The tuning frequency of the distress beacon is ${part2(input)}.")
}

fun part1(input: List<String>): Int {
    val lineHeight = 2000000

    val sensorBeaconPairs = input.map(::parsePointsFromLine)

    val linePointsInSensorRadius = linePointsInSensorRadius(sensorBeaconPairs, lineHeight)
    val beaconsOnLine = sensorBeaconPairs.map { (_, beacon) -> beacon }.filter { it.y == lineHeight }.toSet()

    return (linePointsInSensorRadius - beaconsOnLine).size
}

private val sensorRegex = Regex("""Sensor at x=([^,]+?), y=([^:]+?): closest beacon is at x=([^,]+?), y=([^,]+)""")

fun parsePointsFromLine(line: String): SensorAndBeacon {
    val sensorRegexMatches = sensorRegex.matchEntire(line)
    if (sensorRegexMatches != null) {
        val (sensorX, sensorY, beaconX, beaconY) = sensorRegexMatches.destructured

        return SensorAndBeacon(Point(sensorX.toInt(), sensorY.toInt()), Point(beaconX.toInt(), beaconY.toInt()))
    }
    throw IllegalArgumentException("Input line does not match the expected format: '$line' does not match '$sensorRegex'.")
}

fun linePointsInSensorRadius(sensorBeaconPairs: List<SensorAndBeacon>, lineHeight: Int): Set<Point> =
    sensorBeaconPairs
        .asSequence()
        .map { it.sensor to it.getSensorRadius() }
        .filter { (sensor, radius) -> abs(sensor.y - lineHeight) <= radius }
        .flatMap { (sensor, radius) -> linePointsInSensorRadius(radius, sensor, lineHeight) }
        .toSet()

fun linePointsInSensorRadius(radius: Int, sensor: Point, lineHeight: Int): List<Point> {
    val maxDeltaX = radius - abs(sensor.y - lineHeight)
    println("Sensor is at $sensor, radius is $radius, so the interval at y=$lineHeight is [${sensor.x - maxDeltaX},${sensor.x + maxDeltaX}].")
    return (-maxDeltaX..maxDeltaX).map { deltaX -> Point(sensor.x + deltaX, lineHeight) }
}

fun part2(input: List<String>, searchSpaceSize: Int = 4000000): Long {
    val sensorWithRadius = input.map(::parsePointsFromLine).map { OneNorm.Ball(it.sensor, it.getSensorRadius()) }

    for (x in 0..searchSpaceSize) {
        var point = Point(x, 0)
        val sensorBallsToConsider = sensorWithRadius.toMutableList()

        while (point.y <= searchSpaceSize) {
            val sensorBall = sensorBallsToConsider.firstOrNull { it contains point }
                ?: return calculateTuningFrequency(point)
            sensorBallsToConsider.remove(sensorBall)

            point = getNextPointOnVerticalLineOutsideOfBall(point, sensorBall)
        }
    }

    throw NoSuchElementException("The whole search space is covered by the sensors.")
}

private fun getNextPointOnVerticalLineOutsideOfBall(point: Point, sensorBall: Norm.Ball): Point {
    val distanceToSphere = sensorBall.distanceToSphere(point)

    val verticalDistanceToSensor = abs(point.y - sensorBall.point.y)

    return if (sensorBall.point.y > point.y) {
        // reflect point on the horizontal line through the sensor and go further down until you leave the sphere
        // of the detection ball around the sensor
        Point(point.x, point.y + 2 * verticalDistanceToSensor + distanceToSphere + 1)
    } else {
        // go further down until you leave the sphere of the detection ball around the sensor
        Point(point.x, point.y + distanceToSphere + 1)
    }
}

fun calculateTuningFrequency(beacon: Point) = with(beacon) { x * 4000000L + y }