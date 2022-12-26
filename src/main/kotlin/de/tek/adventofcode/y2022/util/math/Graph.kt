package de.tek.adventofcode.y2022.util.math

class Graph<T>(edges: List<Edge<T>>) {
    private val neighbourMap = edges.groupBy(Edge<T>::from) { it }

    constructor(edges: Set<Pair<T, T>>) : this(edges.map { Edge(it, 1) })

    fun vertices(): Set<T> = neighbourMap.keys

    fun toPath(vertices: List<T>): List<Edge<T>> {
        if (vertices.size < 2) return emptyList()

        return vertices.zip(vertices.drop(1)).map { (from, to) ->
            neighbourMap[from]?.firstOrNull { it.to == to }
                ?: throw IllegalArgumentException("Vertices $from and $to do not form an edge ein the graph.")
        }
    }

    /**
     * Finds a shortest path between the given vertices in the graph. If there is no path, an empty list is returned.
     * Otherwise, returns a list of consecutive edges from start to end.
     */
    fun findShortestPath(start: T, end: T): List<Edge<T>> {
        val distances = mutableMapOf(start to 0)
        val predecessor = mutableMapOf<T, Edge<T>?>(start to null)

        val verticesToVisit = mutableListOf(start)

        while (verticesToVisit.isNotEmpty()) {
            val vertex = verticesToVisit.removeFirst()
            val distance = distances[vertex]
                ?: throw IllegalStateException("Vertex $vertex should have a distance at this point.")

            val outgoingEdges = neighbourMap[vertex] ?: emptyList()

            for (edge in outgoingEdges) {
                val neighbour = edge.to
                val oldDistance = distances[neighbour]
                if (oldDistance == null || oldDistance > distance + 1) {
                    distances[neighbour] = distance + edge.weight
                    predecessor[neighbour] = edge
                    verticesToVisit.add(neighbour)
                }
            }
        }

        val lastEdge = predecessor[end] ?: return emptyList()
        return generateSequence(lastEdge) { edge -> predecessor[edge.from] }.toList().reversed()
    }
}

data class Edge<T>(val from: T, val to: T, val weight: Int) {

    constructor(vertices: Pair<T, T>, weight: Int) : this(vertices.first, vertices.second, weight)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Edge<*>) return false

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from?.hashCode() ?: 0
        result = 31 * result + (to?.hashCode() ?: 0)
        return result
    }
}