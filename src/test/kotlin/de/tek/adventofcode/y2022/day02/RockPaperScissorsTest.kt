package de.tek.adventofcode.y2022.day02

import de.tek.adventofcode.y2022.day02.Choice.Rock as Rock
import de.tek.adventofcode.y2022.day02.Choice.Paper as Paper
import de.tek.adventofcode.y2022.day02.Choice.Scissors as Scissors
import de.tek.adventofcode.y2022.day02.Result.Win as Win
import de.tek.adventofcode.y2022.day02.Result.Lose as Lose
import de.tek.adventofcode.y2022.day02.Result.Draw as Draw

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ChoiceTest : StringSpec({
    "score" {
        forAll(
            row(Rock, 1),
            row(Paper, 2),
            row(Scissors, 3)
        ) { choice, expectedScore ->
            choice.score shouldBe expectedScore
        }
    }

    "scoreAgainst" {
        forAll(
            row(Rock, Rock, 3),
            row(Rock, Paper, 0),
            row(Rock, Scissors, 6),
            row(Paper, Rock, 6),
            row(Paper, Paper, 3),
            row(Paper, Scissors, 0),
            row(Scissors, Rock, 0),
            row(Scissors, Paper, 6),
            row(Scissors, Scissors, 3),
        ) { a, b, expectedScore ->
            (a playAgainst b).score shouldBe expectedScore
        }
    }

    "choiceToResultIn" {
        forAll(
            row(Rock, Draw, Rock),
            row(Rock, Lose, Paper),
            row(Rock, Win, Scissors),
            row(Paper, Win, Rock),
            row(Paper, Draw, Paper),
            row(Paper, Lose, Scissors),
            row(Scissors, Lose, Rock),
            row(Scissors, Win, Paper),
            row(Scissors, Draw, Scissors),
        ) { choice, result, expectedChoice ->
            choice.choiceToResultIn(result) shouldBe expectedChoice
        }
    }
})

class GameTest : StringSpec({
    "scenarios" {
        listOf(
            arrayOf(Rock, Rock, Rock, Rock) to 8,
            arrayOf(Rock, Paper, Paper, Rock, Scissors, Scissors) to 15
        ).map { convertToListOfMoves(*it.first) to it.second }
            .forAll { (moves, expectedScore) ->
                moves.play().otherScore shouldBe expectedScore
            }
    }
})

fun convertToListOfMoves(vararg choices: Choice): List<Pair<Choice, Choice>> {
    if (choices.size.mod(2) != 0) {
        throw IllegalArgumentException("Number of choices must be even.")
    }

    return choices.asList().chunked(2) { Move(it[0], it[1]) }
}

class AlternativeMove : StringSpec( {
    "scenarios" {
        listOf(
            "A Y" to Move(Rock, Rock),
            "B X" to Move(Paper, Rock),
            "C Z" to Move(Scissors, Rock)
        ).forAll { (input, expectedMove) ->
            input.toAlternativeMove() shouldBe expectedMove
        }
    }
})