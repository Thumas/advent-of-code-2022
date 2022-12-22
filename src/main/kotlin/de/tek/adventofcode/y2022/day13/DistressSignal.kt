package de.tek.adventofcode.y2022.day13

import de.tek.adventofcode.y2022.util.readInputLines
import de.tek.adventofcode.y2022.util.splitByBlankLines
import kotlinx.serialization.json.*

sealed class PacketContent : Comparable<PacketContent>

class Integer(val value: Int) : PacketContent() {
    override fun compareTo(other: PacketContent) =
        when (other) {
            is Integer -> this.value.compareTo(other.value)
            is PacketList -> PacketList(this).compareTo(other)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Integer) return false

        return value == other.value
    }

    override fun hashCode() = value

    override fun toString() = value.toString()

    companion object {
        fun from(jsonPrimitive: JsonPrimitive): Integer {
            val item = jsonPrimitive.content.toInt()
            return Integer(item)
        }

    }
}

typealias Packet = PacketList

class PacketList() : PacketContent() {
    private val content = mutableListOf<PacketContent>()

    constructor(vararg elements: PacketContent) : this() {
        content.addAll(elements)
    }

    fun add(item: PacketContent): PacketList {
        content.add(item)
        return this
    }

    override fun compareTo(other: PacketContent): Int {
        val otherList = if (other is PacketList) other else PacketList(other)

        if (this.content.isEmpty() && otherList.content.isEmpty()) return 0

        val contentSizeDifference = this.content.size - otherList.content.size

        val resultFromContentComparison = compareContent(otherList)
        return resultFromContentComparison ?: contentSizeDifference
    }

    private fun compareContent(otherList: PacketList) =
        this.content.zip(otherList.content).asSequence().map { it.first.compareTo(it.second) }.filter { it != 0 }
            .firstOrNull()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PacketList) return false

        if (content != other.content) return false

        return true
    }

    override fun hashCode() = content.hashCode()

    override fun toString() = "[" + content.joinToString(",") + "]"

    companion object {
        fun from(jsonElement: JsonElement): Packet {
            val result = Packet()
            for (element in jsonElement.jsonArray) {
                when (element) {
                    is JsonArray -> result.add(Packet.from(element))
                    is JsonPrimitive -> result.add(Integer.from(element))

                    else -> throw IllegalArgumentException("Invalid list in packet string: $element.")
                }
            }

            return result
        }
    }
}

fun main() {
    val input = readInputLines(PacketList::class)

    println("The sum of the indices of the pairs that are in right order is ${part1(input)}.")

    println("The decoder key is ${part2(input)}.")
}

fun part1(input: List<String>): Int {
    val packetPairs =
        splitByBlankLines(input).map { parseTwoLinesAsPackets(it) }.map { Pair(it[0], it[1]) }

    return packetPairs.map { pair -> pair.first <= pair.second }.withIndex().filter { it.value }.sumOf { it.index + 1 }
}

fun part2(input: List<String>): Int {
    val packets = parsePackets(input)

    fun createDivider(int: Int) = Packet(Packet(Integer(int)))
    val firstDivider = createDivider(2)
    val secondDivider = createDivider(6)

    val packetsWithDividers = packets + firstDivider + secondDivider
    val sortedPackets = packetsWithDividers.sorted()

    fun Iterable<Packet>.oneBasedIndexOf(element: PacketContent) = indexOf(element) + 1

    val firstDividerIndex = sortedPackets.oneBasedIndexOf(firstDivider)
    val secondDividerIndex = sortedPackets.oneBasedIndexOf(secondDivider)

    return firstDividerIndex * secondDividerIndex
}

private fun parseTwoLinesAsPackets(it: List<String>) =
    it.take(2).map(Json::parseToJsonElement).map(Packet::from)

fun parsePackets(input: List<String>) =
    input.filter { it.isNotBlank() }.map(Json::parseToJsonElement).map(Packet::from)
