package de.tek.adventofcode.y2022.util.math

class Graph<T>(edges: List<Edge<T>>) {
    private val neighbourMap = edges.groupBy(Edge<T>::from) { it }

    constructor(edges: Set<Pair<T, T>>) : this(edges.map { Edge(it, 1) })

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

    fun maximizeValueAlongPathsWithLimitedCosts(
        start: T,
        costFunction: (Edge<T>) -> Int,
        valueFunction: (Int, T) -> Int,
        costLimit: Int
    ): List<Edge<T>> {
        class CostValuePath(var cost: Int = 0, var value: Int = 0, edges: List<Edge<T>> = emptyList()) :
            Path<T>(edges) {

            private val visitedVertices = edges.flatMap { listOf(it.from, it.to) }.toMutableSet()

            infix fun contains(vertex: T) = vertex in visitedVertices

            override fun add(edge: Edge<T>): Boolean {
                val result = super.add(edge)

                if (result) {
                    visitedVertices.add(edge.to)
                    cost += costFunction(edge)
                    value += valueFunction(cost, edge.to)
                }

                return result
            }

            override fun removeLastEdge(): Edge<T> {
                val removedEdge = super.removeLastEdge()
                value -= valueFunction(cost, removedEdge.to)
                cost -= costFunction(removedEdge)

                visitedVertices.remove(removedEdge.to)

                return removedEdge
            }

            override fun copy() = CostValuePath(cost, value, edges)
        }

        var foundPath: CostValuePath? = null
        var currentPath = CostValuePath()

        fun maximizePath() {
            if (foundPath == null || foundPath!!.value < currentPath.value) {
                println("New candidate found, pressure is ${currentPath.value}, path is: ${currentPath.edges}.")
                foundPath = currentPath
                currentPath = currentPath.copy()
            }
        }

        val edgesToVisit = neighbourMap[start]?.toMutableList() ?: return emptyList()

        while (edgesToVisit.isNotEmpty()) {
            val edge = edgesToVisit.removeLast()

            // backtracking, if the edge does not fit the current last vertex
            while (!currentPath.matches(edge) ) {
                maximizePath()
                currentPath.removeLastEdge()
            }

            val neighbour = edge.to

            if (currentPath contains neighbour) {
                maximizePath()
                continue
            }

            val newCost = currentPath.cost + costFunction(edge)

            if (newCost <= costLimit) {
                currentPath.add(edge)

                if (newCost == costLimit) {
                    maximizePath()
                    continue
                }

                val outgoingEdges = neighbourMap[neighbour]?.filterNot { currentPath contains it.to } ?: emptyList()

                edgesToVisit.addAll(outgoingEdges)
            }
        }

        maximizePath()
        return foundPath?.edges ?: emptyList()
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

open class Path<T>(edges: List<Edge<T>> = emptyList()) {
    val edges: MutableList<Edge<T>> = edges.toMutableList()

    fun matches(edge: Edge<T>): Boolean {
        val lastVertex = edges.lastOrNull()?.to
        return lastVertex == null || lastVertex == edge.from
    }

    open fun add(edge: Edge<T>) = matches(edge) && edges.add(edge)

    open fun removeLastEdge() = edges.removeLast()

    open fun copy() = Path(edges.toMutableList())
}