package de.tek.adventofcode.y2022.day10

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class CpuTest : StringSpec({
    "Given new CPU, getSignalStrength returns 0." {
        Cpu().getSignalStrength() shouldBe 0
    }

    "Given CPU and execute noop, getSignalStrength returns 1." {
        val cpu = Cpu()
        cpu.execute(Instruction.Noop)

        cpu.getSignalStrength() shouldBe 1
    }

    "Given CPU and execute addx with a value, getSignalStrength returns that value plus 1 doubled." {
        forAll(
            row(1),
            row(2),
            row(10),
            row(-1),
            row(-113)
        ) { value ->
            val cpu = Cpu()
            cpu.execute(Instruction.AddX(value))

            cpu.getSignalStrength() shouldBe 2 * (1 + value)
        }
    }

    "When executing addx with a value, the collected signal strengths are 1 and 2." {
        forAll(
            row(1),
            row(2),
            row(10),
            row(-1),
            row(-113)
        ) { value ->
            val cpu = Cpu()

            val signalStrengths = mutableListOf<Int>()
            cpu.registerCycleListener({ true }) { signalStrengths.add(cpu.getSignalStrength()) }

            cpu.execute(Instruction.AddX(value))

            signalStrengths shouldContainExactly listOf(1, 2)
        }
    }

    "When executing {0} noops and collecting the signal strength very 10th cycle, the collected signal strengths are as expected {1}." {
        forAll(
            row(1, listOf()),
            row(2, listOf()),
            row(10, listOf(10)),
            row(15, listOf(10)),
            row(21, listOf(10,20)),
            row(56, listOf(10,20,30,40,50)),
        ) { numberOfNoOps, expectedSignalStrengths ->
            val cpu = Cpu()

            val signalStrengths = mutableListOf<Int>()
            cpu.registerCycleListener({ it.mod(10) == 0 }) { signalStrengths.add(cpu.getSignalStrength()) }

            repeat(numberOfNoOps) { cpu.execute(Instruction.Noop) }

            signalStrengths shouldContainExactly expectedSignalStrengths
        }
    }


})