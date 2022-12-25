package de.tek.adventofcode.y2022.util.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GraphTest : StringSpec({
    "Given linear graph and its ends as start and end, the shortest path is the whole line." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5))

        graph.findShortestPath(1, 5).visitedVertices() shouldBe listOf(1, 2, 3, 4, 5)
    }

    "Given a bi-directional cycle and some start and end, the shortest path is the edge." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 1, 5 to 4, 4 to 3, 3 to 2, 2 to 1, 1 to 5))

        graph.findShortestPath(1, 5).visitedVertices() shouldBe listOf(1, 5)
    }

    "Given a line from start to end and a shortcut, the shortest path is the shortcut." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 1 to 5))

        graph.findShortestPath(1, 5).visitedVertices() shouldBe listOf(1, 5)
    }

    "Given a line from start to end and a shortcut with an intermediate stop, the shortest path is the shortcut." {
        val graph = Graph(setOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 1 to 3, 3 to 5))

        graph.findShortestPath(1, 5).visitedVertices() shouldBe listOf(1, 3, 5)
    }
})

private fun <T> Iterable<Edge<T>>.visitedVertices(): List<T> {
    val vertices = mutableListOf<T>()

    val iterator = this.iterator()
    if (iterator.hasNext()) {
        val firstEdge = iterator.next()
        vertices.add(firstEdge.from)
        vertices.add(firstEdge.to)
    }

    while (iterator.hasNext()) {
        val edge = iterator.next()
        if (vertices.last() != edge.from) {
            throw IllegalStateException("Edges are not consecutive. Last end vertex was ${vertices.last()}, next start vertex was ${edge.from}.")
        }
        vertices.add(edge.to)
    }

    return vertices
}