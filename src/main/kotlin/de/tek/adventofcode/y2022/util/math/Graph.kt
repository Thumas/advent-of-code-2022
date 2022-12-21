package de.tek.adventofcode.y2022.util.math

class Graph<T>(edges: Set<Pair<T, T>>) {
    private val neighbourMap = edges.groupBy(Pair<T, T>::first, Pair<T, T>::second)

    fun findShortestPath(start: T, end: T): List<T> {
        val distances = mutableMapOf(start to 0)
        val predecessor = mutableMapOf<T, T?>(start to null)

        val verticesToVisit = mutableListOf(start)

        while (verticesToVisit.isNotEmpty()) {
            val vertex = verticesToVisit.removeFirst()
            val distance = distances[vertex]
                ?: throw IllegalStateException("Vertex $vertex should have a distance at this point.")

            val neighbours = neighbourMap[vertex] ?: emptyList()

            for (neighbour in neighbours) {
                val oldDistance = distances[neighbour]
                if (oldDistance == null || oldDistance > distance + 1) {
                    distances[neighbour] = distance + 1
                    predecessor[neighbour] = vertex
                    verticesToVisit.add(neighbour)
                }
            }
        }

        return generateSequence(end) { vertex -> predecessor[vertex]}.toList().reversed()
    }
}