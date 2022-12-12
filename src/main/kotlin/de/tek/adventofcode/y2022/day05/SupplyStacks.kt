package de.tek.adventofcode.y2022.day05

import de.tek.adventofcode.y2022.util.readInputLines

typealias Item = Char

data class MoveCommand(val from: Int, val to: Int, val numberOfCrates: Int) {
    companion object {
        fun fromDescription(description: String): MoveCommand? {
            val parsedInts = parseByRegex(description)
            return if (parsedInts != null && parsedInts.size == 3) {
                MoveCommand(correctOffset(parsedInts[1]), correctOffset(parsedInts[2]), parsedInts[0])
            } else {
                null
            }
        }

        private fun correctOffset(position: Int) = position - 1

        private fun parseByRegex(it: String) =
            """move (\d+) from (\d+) to (\d+)""".toRegex().matchEntire(it)?.groupValues?.drop(1)?.map { it.toInt() }
    }

}

class SupplyStacks(numberOfStacks: Int) {
    private val stacks = Array<MutableList<Item>>(numberOfStacks) { mutableListOf() }

    fun loadLineOfCrates(lineOfCrates: Array<Item?>) {
        lineOfCrates.mapIndexed { i, item -> if (item != null) put(i, item) }
    }

    private fun put(stack: Int, item: Item) {
        stacks[stack].add(item)
    }

    fun executeInSingleSteps(command: MoveCommand) {
        repeat(command.numberOfCrates) {
            val crane = loadCrane(command.from, 1)
            unloadCrane(command.to, crane)
        }
    }

    fun executeAsBatch(command: MoveCommand) {
        val crane = loadCrane(command.from, command.numberOfCrates)
        unloadCrane(command.to, crane)
    }

    private fun loadCrane(targetStack: Int, numberOfCrates: Int): ArrayDeque<Item> {
        val crane = ArrayDeque<Item>(numberOfCrates)
        repeat(numberOfCrates) {
            crane.addFirst(stacks[targetStack].removeLast())
        }
        return crane
    }

    private fun unloadCrane(
        targetStack: Int,
        crane: ArrayDeque<Item>
    ) {
        stacks[targetStack].addAll(crane)
    }

    fun getTopCrates() = stacks.map { if (it.isEmpty()) ' ' else it.last() }

    override fun toString(): String {
        return stacks.foldIndexed("SupplyStacks@${super.toString()}:\n") { stackNo, string, stack ->
            string + "$stackNo: " + stack.joinToString(" ") { "[$it]" } + "\n"
        }
    }

    companion object {
        fun fromDescription(description: List<String>): SupplyStacks {
            if (description.isEmpty()) {
                throw IllegalArgumentException("The crate description must at least contain the footer line.")
            }

            val lines = description.toMutableList()
            val stackNumbering = lines.removeLast()
            val numberOfStacks = parseNumberOfColumns(stackNumbering)

            val stacks = SupplyStacks(numberOfStacks)
            lines.reversed().asSequence().map { parseCrates(it) }.forEach { stacks.loadLineOfCrates(it) }

            return stacks
        }

        private fun parseCrates(description: String) =
            splitColumns(description).map { it.removeSurrounding("[", "]") }.map { if (it.isEmpty()) null else it[0] }
                .toTypedArray()

        private fun parseNumberOfColumns(footerLine: String) =
            splitColumns(footerLine).size

        private fun splitColumns(line: String) =
            line.chunked(4).map { it.trim() }
    }
}


fun main() {
    val input = readInputLines(SupplyStacks::class)

    fun processInstructions(input: List<String>, craneFunction: SupplyStacks.(MoveCommand) -> Unit): String {
        val stackDescription = input.takeWhile { it.isNotBlank() }
        val stacks = SupplyStacks.fromDescription(stackDescription)

        val moveDescription = input.takeLastWhile { it.isNotBlank() }

        moveDescription.mapNotNull { MoveCommand.fromDescription(it) }.forEach { stacks.craneFunction(it) }
        return toString(stacks.getTopCrates())
    }

    fun part1(input: List<String>) = processInstructions(input, SupplyStacks::executeInSingleSteps)
    fun part2(input: List<String>) = processInstructions(input, SupplyStacks::executeAsBatch)

    println("Using the CrateMover 9000, the top crates are ${part1(input)}.")
    println("Using the CrateMover 9001, The top crates are ${part2(input)}.")
}

fun toString(crates: List<Item>) = String(crates.toCharArray())