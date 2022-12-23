package de.tek.adventofcode.y2022.util.math

import kotlin.math.abs

object InfinityNorm : Norm {
    override fun sizeOf(vector: Vector) = listOf(vector.x, vector.y).maxOf(::abs)
    override fun ballOfRadius(point: Point, radius: Int) =
        (0..radius).flatMap { x ->
            (0..radius)
                .flatMap { y ->
                    listOf(Vector(x, y), Vector(-x, y), Vector(x, -y), Vector(-x, -y)).map { point + it }
                }
        }.toSet()
}