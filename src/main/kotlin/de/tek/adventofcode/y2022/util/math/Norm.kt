package de.tek.adventofcode.y2022.util.math

abstract class Norm {
    abstract fun sizeOf(vector: Vector): Int

    abstract fun ballOfRadiusAsSequence(point: Point, radius: Int): Sequence<Point>

    infix fun Point.distanceTo(other: Point) = sizeOf(other - this)

    inner class Ball(val point: Point, val radius: Int) {
        infix fun contains(other: Point) = point distanceTo other <= radius

        fun distanceToSphere(other: Point) = radius - (point distanceTo other)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Ball) return false

            if (point != other.point) return false
            if (radius != other.radius) return false

            return true
        }

        override fun hashCode(): Int {
            var result = point.hashCode()
            result = 31 * result + radius
            return result
        }

        override fun toString(): String {
            return "${this@Norm::class.simpleName}-Ball(point=$point, radius=$radius)"
        }
    }
}