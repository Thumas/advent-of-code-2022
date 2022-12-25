package de.tek.adventofcode.y2022.util.math

import kotlin.math.abs

object InfinityNorm : Norm() {
    override fun sizeOf(vector: Vector) = listOf(vector.x, vector.y).maxOf(::abs)
    override fun ballOfRadiusAsSequence(point: Point, radius: Int): Sequence<Point> =
        (0..radius).asSequence().flatMap { x ->
            (0..radius).asSequence()
                .flatMap { y ->
                    listOf(Vector(x, y), Vector(-x, y), Vector(x, -y), Vector(-x, -y)).map { point + it }
                }
        }
}