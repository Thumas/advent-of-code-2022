package de.tek.adventofcode.y2022.day05

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class SupplyStacksTest : StringSpec({
    "loadFromDescription" {
        forAll(
            row(
                listOf(
                    "    [D]    ",
                    "[N] [C]    ",
                    "[Z] [M] [P]",
                    " 1   2   3 "
                ), "NDP"
            ),
            row(listOf(" 1 "), " "),
            row(
                listOf(
                    "        [Z]",
                    "        [N]",
                    "        [D]",
                    "[C] [M] [P]",
                    " 1   2   3 "
                ), "CMZ"
            ),
        ) { input, expectedTopCrates ->
            toString(SupplyStacks.fromDescription(input).getTopCrates()) shouldBe expectedTopCrates
        }
    }

    "executeInSingleSteps" {
        forAll(
            row(MoveCommand(0, 1, 2), " ZP"),
            row(MoveCommand(2, 0, 1), "PD "),
            row(MoveCommand(1, 2, 3), "N M")
        ) { command, expectedTopCrates ->
            val stacks = SupplyStacks(3)
            stacks.loadLineOfCrates(arrayOf('Z', 'M', 'P'))
            stacks.loadLineOfCrates(arrayOf('N', 'C'))
            stacks.loadLineOfCrates(arrayOf(null, 'D'))

            stacks.executeInSingleSteps(command)

            toString(stacks.getTopCrates()) shouldBe expectedTopCrates
        }
    }

    "executeAsBatch" {
        forAll(
            row(MoveCommand(0, 1, 2), " NP"),
            row(MoveCommand(2, 0, 1), "PD "),
            row(MoveCommand(1, 2, 1), "NCD")
        ) { command, expectedTopCrates ->
            val stacks = SupplyStacks(3)
            stacks.loadLineOfCrates(arrayOf('Z', 'M', 'P'))
            stacks.loadLineOfCrates(arrayOf('N', 'C'))
            stacks.loadLineOfCrates(arrayOf(null, 'D'))

            stacks.executeAsBatch(command)

            toString(stacks.getTopCrates()) shouldBe expectedTopCrates
        }
    }
})

class MoveCommandTest : StringSpec({
    "fromDescription" {
        forAll(
            row("move 5 from 4 to 5", MoveCommand(3, 4, 5)),
            row("move 2 from 5 to 8", MoveCommand(4, 7, 2)),
            row("move 2 from 9 to 1", MoveCommand(8, 0, 2)),
            row("move 1 from 5 to 3", MoveCommand(4, 2, 1)),
            row("move 10 from 5 to 8", MoveCommand(4, 7, 10)),
        ) { input, expectedCommand ->
            MoveCommand.fromDescription(input) shouldBe expectedCommand
        }
    }
})