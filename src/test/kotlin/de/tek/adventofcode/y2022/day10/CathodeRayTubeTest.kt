package de.tek.adventofcode.y2022.day10

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class CpuTest : StringSpec({

    "Given new device, getSignalStrength returns 0." {
        Device(listOf()).getSignalStrength() shouldBe 0
    }

    "Given device and execute noop, getSignalStrength returns 2." {
        val device = Device(listOf(Instruction.Noop()))
        device.run()

        device.getSignalStrength() shouldBe 1
    }

    "When executing addx with a value, the signal strengths collected at during the cycles are 1 and 2." {
        forAll(
            row(1),
            row(2),
            row(10),
            row(-1),
            row(-113)
        ) { value ->
            val clock = Clock()
            val signalStrengths = mutableListOf<Int>()
            var device: Device? = null
            // first register this test listener, so we get the signal strength before finishing the cycle
            clock.registerCycleListener { signalStrengths.add(device?.getSignalStrength() ?: 0) }
            device = Device(clock = clock, program = listOf(Instruction.AddX(value)))

            device!!.run()

            signalStrengths shouldContainExactly listOf(1, 2)
        }
    }

    "When executing addx with a value, the signal strengths collected at the end of the cycles are the value plus one doubled." {
        forAll(
            row(1),
            row(2),
            row(10),
            row(-1),
            row(-113)
        ) { value ->
            val clock = Clock()
            val signalStrengths = mutableListOf<Int>()
            val device = Device(clock = clock, program = listOf(Instruction.AddX(value)))
            clock.registerCycleListener { signalStrengths.add(device.getSignalStrength()) }

            device.run()

            signalStrengths shouldContainExactly listOf(1, 2*(1+value))
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
            val clock = Clock()
            val device = Device(clock = clock, program = List(numberOfNoOps) { Instruction.Noop() })

            val signalStrengths = mutableListOf<Int>()
            clock.registerCycleListener({ it.mod(10) == 0 }) { signalStrengths.add(device.getSignalStrength()) }

            device.run()

            signalStrengths shouldContainExactly expectedSignalStrengths
        }
    }
})

class CathodeRayTubeTest : StringSpec({
    "Given the register is -2 and drawing all pixels, then visualize returns an empty screen." {
        val cathodeRayTube = CathodeRayTube { -2 }

        repeat(40*6) {
            cathodeRayTube.draw()
        }

        cathodeRayTube.visualize() shouldBe """
            ........................................
            ........................................
            ........................................
            ........................................
            ........................................
            ........................................
        """.trimIndent()
    }
    
    "Given the register is 1 and drawing all pixels, then visualize returns a screen with the sprite on the left border." {
        val cathodeRayTube = CathodeRayTube { 1 }

        repeat(40*6) {
            cathodeRayTube.draw()
        }

        cathodeRayTube.visualize() shouldBe """
            ###.....................................
            ###.....................................
            ###.....................................
            ###.....................................
            ###.....................................
            ###.....................................
        """.trimIndent()
    }
    
    "Given the register progressing in 1-steps and drawing all pixels, then visualize returns a full first line." {
        val register = Register(1)
        val cathodeRayTube = CathodeRayTube { register.getAndIncrement() }

        repeat(40*6) {
            cathodeRayTube.draw()
        }

        cathodeRayTube.visualize() shouldBe """
            ########################################
            ........................................
            ........................................
            ........................................
            ........................................
            ........................................
        """.trimIndent()
    }

    "Given the register progressing in 1-steps, resetting after 40 and drawing all pixels, then visualize returns a full screen." {
        val register = Register(0)
        val cathodeRayTube = CathodeRayTube {
            register.getAndIncrement()
            register.compareAndExchange(40, 0)
        }

        repeat(40*6) {
            cathodeRayTube.draw()
        }

        cathodeRayTube.visualize() shouldBe """
            ########################################
            ########################################
            ########################################
            ########################################
            ########################################
            ########################################
        """.trimIndent()
    }
})