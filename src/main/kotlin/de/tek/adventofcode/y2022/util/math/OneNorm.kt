package de.tek.adventofcode.y2022.util.math

import kotlin.math.abs

object OneNorm : Norm {
    override fun sizeOf(vector: Vector) = listOf(vector.x, vector.y).map(::abs).sum()

    override fun ballOfRadius(point: Point, radius: Int) =
        (0..radius).flatMap { x ->
            (0..radius)
                .filter { y -> x + y <= radius }
                .flatMap { y ->
                    listOf(Vector(x, y), Vector(-x, y), Vector(x, -y), Vector(-x, -y)).map { point + it }
                }
        }.toSet()


}