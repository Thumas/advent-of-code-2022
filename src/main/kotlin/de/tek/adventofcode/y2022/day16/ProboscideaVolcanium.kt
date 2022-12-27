package de.tek.adventofcode.y2022.day16

import de.tek.adventofcode.y2022.util.algorithms.permutations
import de.tek.adventofcode.y2022.util.algorithms.subsets
import de.tek.adventofcode.y2022.util.math.Edge
import de.tek.adventofcode.y2022.util.math.Graph
import de.tek.adventofcode.y2022.util.readInputLines

class Valve(val name: String, val flowRate: Int) {
    private val reachableValves = mutableListOf<Valve>()

    fun addReachableValves(valves: List<Valve>) = reachableValves.addAll(valves)

    fun getReachableValves() = reachableValves.toList()

    override fun toString(): String {
        return "Valve(name='$name', flowRate=$flowRate, reachableValves=${reachableValves.map(Valve::name)})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Valve) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        private val valveRegex = Regex("""Valve ([^ ]+) has flow rate=(\d+)""")
        fun parseFrom(string: String): Valve {
            val valveRegexMatches = valveRegex.matchEntire(string)
            if (valveRegexMatches != null) {
                val (name, flowRate) = valveRegexMatches.destructured
                return Valve(name, flowRate.toInt())
            }
            throw IllegalArgumentException("Cannot parse valve from $string. Expected format is $valveRegex.")
        }
    }
}

class Volcano private constructor(valves: Collection<Valve>, private val startValve: Valve) {

    private val tunnelGraph: Graph<Valve>
    private val valvePathGraph: Graph<Valve>

    init {
        val tunnels = valves.flatMap { it.getReachableValves().map { other -> it to other } }.toSet()

        tunnelGraph = Graph(tunnels)

        val workingValves = valves.filter { it.flowRate > 0 }
        val pathsBetweenVertices = (workingValves + startValve).flatMap { from ->
            workingValves.filter { it != from }
                .map { to -> Edge(from, to, tunnelGraph.findShortestPath(from, to).size) }
                .filter { it.weight > 0 }
        }

        valvePathGraph = Graph(pathsBetweenVertices)
    }

    fun calculateMaximumPressureRelease(): Int {
        val valvesWithoutStartValve = valvePathGraph.vertices() - startValve
        return allFeasibleValvePaths(valvesWithoutStartValve, 30)
            .map { (valvePathWithoutStartValve, _) -> valvePathWithoutStartValve.calculateReleasedPressure(30) }
            .max()
    }

    private fun allFeasibleValvePaths(
        valvesWithoutStartValve: Set<Valve>,
        timeLimit: Int
    ): Sequence<Pair<ValvePath, ValveSet>> =
        valvesWithoutStartValve.permutations { valves ->
            toValvePathEdges(valves).sumOf { edge -> edge.weight + 1 } > timeLimit
        }.filter { it.first.isNotEmpty() }
            .map { ValvePath(it.first) to ValveSet.from(it.second) }

    private inner class ValvePath(private val valves: List<Valve>) {
        private val code = valves.joinToString("|") { it.name }

        val size = valves.size

        fun toValveSet() = ValveSet.from(valves.toSet())

        fun calculateReleasedPressure(timeLimit: Int): Int {
            var timeLeft = timeLimit
            return toValvePathEdges(valves).sumOf { edge ->
                timeLeft -= edge.weight + 1
                timeLeft * edge.to.flowRate
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ValvePath) return false

            if (code != other.code) return false

            return true
        }

        override fun hashCode(): Int {
            return code.hashCode()
        }

        override fun toString() = code
    }

    class ValveSet private constructor(private val valves: Set<Valve>) {
        private val code = encode(valves)

        fun subsets() = valves.subsets().map { from(it) }

        val size = valves.size

        override fun toString() = code

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ValveSet) return false

            if (code != other.code) return false

            return true
        }

        override fun hashCode(): Int {
            return code.hashCode()
        }

        companion object {
            private val cache = mutableMapOf<String, ValveSet>()

            fun from(valves: Set<Valve>): ValveSet {
                return cache.getOrPut(encode(valves)) { ValveSet(valves) }
            }

            private fun encode(valves: Set<Valve>) =
                valves.sortedBy { it.name }.joinToString("|") { it.name }
        }
    }

    private fun toValvePathEdges(valves: List<Valve>) = valvePathGraph.toPath(listOf(startValve) + valves)

    fun calculateMaximumPressureReleaseWithHelp(): Int {
        val valvesWithoutStartValve = valvePathGraph.vertices() - startValve

        val allPathsAndTheirComplements = allFeasibleValvePaths(valvesWithoutStartValve, 26).toList()

        val pathPressureMap =
            allPathsAndTheirComplements
                .map { it.first }
                .associateWith { path -> path.calculateReleasedPressure(26) }

        val numberOfValvesWithoutTheStartValve = valvesWithoutStartValve.size
        val valveSubsetToMaximumPressureForAllPathsInComplementMap =
            valveSubsetToMaximumPressureForAllPathsInComplementMap(
                allPathsAndTheirComplements,
                numberOfValvesWithoutTheStartValve,
                pathPressureMap
            )


        val nonFullPathPressures =
            pathPressureMap.filter { (path, _) -> path.size <= numberOfValvesWithoutTheStartValve / 2 }.asSequence()

        return nonFullPathPressures
            .map { (path, pressure) -> path.toValveSet() to pressure }
            .map { (valveSet, pressure) ->
                val otherPressure = (valveSubsetToMaximumPressureForAllPathsInComplementMap[valveSet]
                    ?: throw IllegalStateException("No maximum pressure for paths in complement calculated for subset $valveSet."))
                otherPressure + pressure
            }.max()
    }

    private fun valveSubsetToMaximumPressureForAllPathsInComplementMap(
        allPathsAndTheirComplements: List<Pair<ValvePath, ValveSet>>,
        numberOfValvesWithoutTheStartValve: Int,
        pathPressureMap: Map<ValvePath, Int>
    ): Map<ValveSet, Int> {
        // here we get a map from subsets of valves to all paths that contain ALL remaining valves
        // in the next step, for each subset, we have to multiply its paths to all subsets of the subset
        val valveSubsetToFullPathsInComplementMap = allPathsAndTheirComplements.groupBy({ it.second }) { it.first }

        return allValveSubsetToAllPathsInComplement(
            valveSubsetToFullPathsInComplementMap,
            numberOfValvesWithoutTheStartValve
        ).mapValues { (_, encodedPaths) -> maximumPressure(encodedPaths, pathPressureMap) }
    }

    // we only have to consider paths of at most half the maximum length for the elephant path, since we get
    // the other combinations by symmetry
    private fun allValveSubsetToAllPathsInComplement(
        valveSubsetToFullPathsInComplementMap: Map<ValveSet, List<ValvePath>>,
        numberOfValvesWithoutTheStartValve: Int
    ) = valveSubsetToFullPathsInComplementMap
        .flatMap { (valveSet, paths) ->
            valveSet
                .subsets()
                .filter { it.size in 1..numberOfValvesWithoutTheStartValve / 2 }
                .map { it to paths }
        }.groupBy({ it.first }) { it.second }
        .mapValues { it.value.flatten() }

    private fun maximumPressure(
        encodedPaths: List<ValvePath>,
        pathPressureMap: Map<ValvePath, Int>
    ) = encodedPaths.maxOfOrNull {
        pathPressureMap[it] ?: throw IllegalStateException("No pressure for path $it calculated.")
    } ?: 0

    companion object {
        private val valveAndTunnelsRegex = Regex("(.*?); tunnels* leads* to valves* (.*)")
        fun parseFrom(input: List<String>, startValveName: String): Volcano {
            val valvesToConnectedValveNamesMap =
                input.mapNotNull(valveAndTunnelsRegex::matchEntire).associate(::valveToConnectedValveNames)
            val valveNameToValveMap = valvesToConnectedValveNamesMap.keys.associateBy { it.name }

            makeValveConnections(valvesToConnectedValveNamesMap, valveNameToValveMap)

            val startValve = valveNameToValveMap[startValveName]
                ?: throw NoSuchElementException("There is no start valve $startValveName.")
            return Volcano(valvesToConnectedValveNamesMap.keys, startValve)
        }

        private fun makeValveConnections(
            valvesToConnectedValveNamesMap: Map<Valve, List<String>>, valveNameToValveMap: Map<String, Valve>
        ) {
            valvesToConnectedValveNamesMap.mapValues { (_, names) ->
                names.map {
                    valveNameToValveMap[it] ?: throw IllegalArgumentException("Tunnel to unknown valve $it.")
                }
            }.forEach { (valve, connectedValves) ->
                valve.addReachableValves(connectedValves)
            }
        }

        private fun valveToConnectedValveNames(result: MatchResult): Pair<Valve, List<String>> {
            val (valveString, tunnelEndpointsString) = result.destructured
            val valve = Valve.parseFrom(valveString)
            val connectedValveNames = tunnelEndpointsString.split(",").map(String::trim)

            return valve to connectedValveNames
        }
    }
}

fun main() {
    val input = readInputLines(Volcano::class)

    println("The most pressure you can release alone is ${part1(input)}.")
    println("The most pressure you can release with the help of an elephant is ${part2(input)}.")
}

fun part1(input: List<String>): Int {
    return Volcano.parseFrom(input, "AA").calculateMaximumPressureRelease()
}

fun part2(input: List<String>): Int {
    return Volcano.parseFrom(input, "AA").calculateMaximumPressureReleaseWithHelp()
}