package de.tek.adventofcode.y2022.util.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GraphTest : StringSpec({
    "Given linear graph and its ends as start and end, the shortest path is the whole line." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5))

        graph.findShortestPath(1,5) shouldBe listOf(1,2,3,4,5)
    }

    "Given a bi-directional cycle and some start and end, the shortest path is the edge." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 1, 5 to 4, 4 to 3, 3 to 2, 2 to 1, 1 to 5))

        graph.findShortestPath(1,5) shouldBe listOf(1,5)
    }

    "Given a line from start to end and a shortcut, the shortest path is the shortcut." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 1 to 5))

        graph.findShortestPath(1,5) shouldBe listOf(1,5)
    }

    "Given a line from start to end and a shortcut with an intermediate stop, the shortest path is the shortcut." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 1 to 3, 3 to 5))

        graph.findShortestPath(1,5) shouldBe listOf(1,3,5)
    }
})
