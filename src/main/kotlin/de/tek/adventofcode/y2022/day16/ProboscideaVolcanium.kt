package de.tek.adventofcode.y2022.day16

import de.tek.adventofcode.y2022.util.algorithms.permutations
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
        return valvesWithoutStartValve.permutations { valves ->
            toValvePathEdges(valves).sumOf { edge -> edge.weight + 1 } > 30
        }.map { valvePathWithoutStartValve ->
            calculateReleasedPressure(valvePathWithoutStartValve)
        }.max()
    }

    private fun calculateReleasedPressure(valvePathWithoutStartValve: List<Valve>): Int {
        var timeLeft = 30
        return toValvePathEdges(valvePathWithoutStartValve).sumOf { edge ->
            timeLeft -= edge.weight + 1
            timeLeft * edge.to.flowRate
        }
    }

    private fun toValvePathEdges(valves: List<Valve>) = valvePathGraph.toPath(listOf(startValve) + valves)

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

    println("The most pressure you can release is ${part1(input)}.")
}

fun part1(input: List<String>): Int {
    return Volcano.parseFrom(input, "AA").calculateMaximumPressureRelease()
}