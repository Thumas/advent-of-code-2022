package de.tek.adventofcode.y2022.day02

import de.tek.adventofcode.y2022.util.readInputLines

enum class Result(val score: Int) {
    Win(6), Lose(0), Draw(3);

    fun invert(): Result =
        when (this) {
            Win -> Lose
            Lose -> Win
            Draw -> Draw
        }
}

enum class Choice(val score: Int) {
    Rock(1), Paper(2), Scissors(3);

    private fun getInferior(): Choice =
        when (this) {
            Rock -> Scissors
            Paper -> Rock
            Scissors -> Paper
        }

    private fun getSuperior(): Choice =
        when (this) {
            Rock -> Paper
            Paper -> Scissors
            Scissors -> Rock
        }


    infix fun playAgainst(other: Choice) =
        when (other) {
            this -> {
                Result.Draw
            }
            this.getSuperior() -> {
                Result.Lose
            }
            else -> {
                Result.Win
            }
        }

    fun choiceToResultIn(result: Result) =
        when (result) {
            Result.Draw -> this
            Result.Lose -> this.getSuperior()
            Result.Win -> this.getInferior()
        }
}


fun Char.toChoice() = with(this.uppercaseChar()) {
    when (this) {
        'A' -> Choice.Rock
        'X' -> Choice.Rock
        'B' -> Choice.Paper
        'Y' -> Choice.Paper
        'C' -> Choice.Scissors
        'Z' -> Choice.Scissors
        else -> throw IllegalArgumentException("$this is not a valid encoding for a choice.")
    }
}

fun Char.toResult() = with(this.uppercaseChar()) {
    when (this) {
        'X' -> Result.Lose
        'Y' -> Result.Draw
        'Z' -> Result.Win
        else -> throw IllegalArgumentException("$this is not a valid encoding for a result.")
    }
}

typealias Move = Pair<Choice, Choice>

fun String.toMove(): Move {
    val choices = this.split(' ').map { it[0] }.map { it.toChoice() }
    return Move(choices[0], choices[1])
}

fun String.toAlternativeMove(): Move {
    val input = this.split(' ').map { it[0] }

    val otherChoice = input[0].toChoice()
    val expectedResultForYou = input[1].toResult()
    val expectedResultForOther = expectedResultForYou.invert()
    val yourMove = otherChoice.choiceToResultIn(expectedResultForOther)

    return Move(otherChoice, yourMove)
}

class Game {
    var otherScore = 0
        private set
    var yourScore = 0
        private set

    fun play(move: Move): Game {
        return play(move.first, move.second)
    }

    private fun play(yourChoice: Choice, otherChoice: Choice): Game {
        otherScore += yourChoice.score + (yourChoice playAgainst otherChoice).score
        yourScore += otherChoice.score + (otherChoice playAgainst yourChoice).score

        return this
    }
}

fun List<Move>.play() = this.fold(Game()) { game, move -> game.play(move) }

fun main() {
    val input = readInputLines(Game::class)

    val finalScore = input.map { it.toMove() }.play().yourScore

    println("The final score using the first encoding would be $finalScore.")

    val alternativeScore = input.map { it.toAlternativeMove() }.play().yourScore

    println("The final score using the alternative encoding would be $alternativeScore.")
}