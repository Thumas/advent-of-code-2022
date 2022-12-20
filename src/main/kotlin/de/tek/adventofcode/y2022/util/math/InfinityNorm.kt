package de.tek.adventofcode.y2022.util.math

import kotlin.math.abs

class InfinityNorm private constructor(){
    fun sizeOf(vector: Vector) = listOf(vector.x, vector.y).maxOf(::abs)

    companion object {
        val instance = InfinityNorm()
    }
}