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

    "Given a linear graph and some consecutive vertices, toPath returns the edges between them." {
        val edges = listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5).map { (from,to) -> Edge(from,to, from)}
        val graph = Graph(edges)

        graph.toPath(listOf(2,3,4)) shouldBe listOf(edges[1], edges[2])
    }

    "Given a tree and some consecutive vertices, toPath returns the edges between them." {
        val edges = listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5, 3 to 6, 6 to 7).map { (from,to) -> Edge(from,to, from)}
        val graph = Graph(edges)

        graph.toPath(listOf(2,3,6,7)) shouldBe listOf(edges[1], edges[4], edges[5])
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