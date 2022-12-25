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

    /*
       (1)-1->(2)
       |       |
       3       1
       |       |
       v       v
       (3)-1->(4)
     */
    "Given two paths from start to end with different weights, the path with the smaller edge-weight sum is returned." {
        val edges = listOf(Edge(1, 2, 1), Edge(2, 4, 1), Edge(1, 3, 3), Edge(3, 4, 1))
        val graph = Graph(edges)

        graph.findShortestPath(1, 4) shouldBe listOf(edges[0], edges[1])
    }

    "Given a linear graph, equal costs and values, maximizeValueAlongPathsWithLimitedCosts returns the sub-path with the cost limit." {
        val edges = listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5).map { (from, to) -> Edge(from, to, 1) }
        val graph = Graph(edges)

        val result = graph.maximizeValueAlongPathsWithLimitedCosts(
            1,
            Edge<Int>::weight,
            { _, _ -> 1 },
            3
        )
        result shouldBe listOf(edges[0], edges[1], edges[2])
    }

    "Given two parallel paths, equal cost but different values, maximizeValueAlongPathsWithLimitedCosts returns the path with higher value." {
        val edges = listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 1 to 20, 20 to 30, 30 to 40, 40 to 5).map { (from, to) ->
            Edge(from, to, 1)
        }
        val graph = Graph(edges)

        val result = graph.maximizeValueAlongPathsWithLimitedCosts(
            1,
            Edge<Int>::weight,
            { _, vertex -> vertex },
            3
        )
        result shouldBe listOf(edges[4], edges[5], edges[6])
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