package de.tek.adventofcode.y2022.day10

import de.tek.adventofcode.y2022.util.readInputLines
import java.util.function.Predicate

class Cpu(private var register: Int = 1) {
    private var currentCycle = 0

    private val cycleListeners = mutableListOf<CycleListener>()

    private class CycleListener(val cyclePredicate: Predicate<Int>, val action: Runnable)

    fun getSignalStrength() = register * currentCycle

    fun registerCycleListener(cyclePredicate: Predicate<Int>, action: Runnable) =
        cycleListeners.add(CycleListener(cyclePredicate, action))

    fun execute(instruction: Instruction) {
        when (instruction) {
            is Instruction.Noop -> {
                startCycle()
            }

            is Instruction.AddX -> {
                startCycle()
                startCycle()
                register += instruction.value
            }
        }
    }

    private fun startCycle() {
        currentCycle++
        informListeners()
    }

    private fun informListeners() {
        cycleListeners.filter { it.cyclePredicate.test(currentCycle) }.forEach { it.action.run() }
    }
}

sealed class Instruction {
    object Noop : Instruction()
    class AddX(val value: Int) : Instruction()
    companion object {
        fun parseFrom(string: String): Instruction {
            val splitString = string.split(" ")
            return when (splitString[0]) {
                Noop::class.simpleName?.lowercase() -> Noop
                AddX::class.simpleName?.lowercase() -> AddX(splitString[1].toInt())
                else -> throw IllegalArgumentException("Unknown instruction: $string.")
            }
        }
    }
}


fun main() {
    val input = readInputLines(Cpu::class)

    fun part1(input: List<String>): Int {
        val cpu = Cpu()

        val signalStrengths = mutableListOf<Int>()
        cpu.registerCycleListener({ (it - 20).mod(40) == 0 }) { signalStrengths.add(cpu.getSignalStrength()) }

        input.map(Instruction::parseFrom).forEach { cpu.execute(it) }

        return signalStrengths.sum()
    }

    println("The sum of the signal strengths is ${part1(input)}.")
}