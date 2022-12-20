package de.tek.adventofcode.y2022.day10

import de.tek.adventofcode.y2022.util.readInputLines
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate
import java.util.function.Supplier

typealias Register = AtomicInteger

class Device constructor(
    private val clock: Clock,
    private val register: Register = Register(1),
    private val tube: CathodeRayTube = CathodeRayTube(register::get),
    private val cpu: Cpu = Cpu(register),
    program: List<Instruction>
) {
    private val instructions = program.toMutableList()

    constructor(instructions: List<Instruction>) : this(clock = Clock(), program = instructions)

    init {
        clock.registerCycleListener(tube::draw)
        clock.registerCycleListener {
            instructions.firstOrNull()?.also { cpu.execute(it) }
        }
    }

    fun run() {
        while (instructions.isNotEmpty()) {
            clock.startCycle()
            instructions.removeIf { it.isCompleted() }
        }
    }

    fun getSignalStrength() = register.get() * clock.currentCycle

    fun showScreen() = tube.visualize()
}

class Clock {
    var currentCycle = 0
        private set

    private class CycleListener(val cyclePredicate: Predicate<Int>, val action: Runnable)

    private val cycleListeners = mutableListOf<CycleListener>()

    fun registerCycleListener(action: Runnable) = registerCycleListener({ true }, action)

    fun registerCycleListener(cyclePredicate: Predicate<Int>, action: Runnable) =
        cycleListeners.add(CycleListener(cyclePredicate, action))

    fun startCycle() {
        currentCycle++
        informListeners()
    }

    private fun informListeners() {
        cycleListeners.filter { it.cyclePredicate.test(currentCycle) }.forEach { it.action.run() }
    }
}

class Cpu(private var register: Register) {
    fun execute(instruction: Instruction) {
        instruction.spendCycle()

        if (instruction.isCompleted()) {
            when (instruction) {
                is Instruction.Noop -> {}
                is Instruction.AddX -> register.getAndAdd(instruction.value)
            }
        }
    }

}

class CathodeRayTube(private val registerReader: Supplier<Int>) {
    private val screen = Array(6) { Array(40) { ' ' } }

    private var currentPixel = 0

    fun draw() {
        val linePosition = currentPixel.mod(40)

        val register = registerReader.get()
        val spritePositions = register - 1..register + 1

        screen[currentPixel / 40][linePosition] = if (linePosition in spritePositions) '#' else '.'

        currentPixel = (currentPixel + 1).mod(40 * 6)
    }

    override fun toString(): String {
        return "CathodeRayTube(screen=${screen.contentToString()}, currentPixel=$currentPixel)"
    }

    fun visualize() = screen.joinToString("\n") { it.joinToString("") }
}

sealed class Instruction(cycles: Int) {
    private var remainingCycles = cycles

    fun spendCycle() {
        remainingCycles--
    }

    fun isCompleted() = remainingCycles <= 0

    class Noop : Instruction(1)
    class AddX(val value: Int) : Instruction(2)

    companion object {
        fun parseFrom(string: String): Instruction {
            val splitString = string.split(" ")
            return when (splitString[0]) {
                Noop::class.simpleName?.lowercase() -> Noop()
                AddX::class.simpleName?.lowercase() -> AddX(splitString[1].toInt())
                else -> throw IllegalArgumentException("Unknown instruction: $string.")
            }
        }
    }
}

fun main() {
    val input = readInputLines(Cpu::class)

    fun part1(input: List<String>): Int {
        val clock = Clock()
        val device = Device(clock, program = input.map(Instruction::parseFrom))

        val signalStrengths = mutableListOf<Int>()
        clock.registerCycleListener({ (it - 20).mod(40) == 0 }) { signalStrengths.add(device.getSignalStrength()) }

        device.run()

        return signalStrengths.sum()
    }

    fun part2(input: List<String>): String {
        val device = Device(input.map(Instruction::parseFrom))

        device.run()

        return device.showScreen()
    }

    println("The sum of the signal strengths is ${part1(input)}.")
    println("The screen shows:\n${part2(input)}")
}