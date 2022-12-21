package de.tek.adventofcode.y2022.day11

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class OperandTest : StringSpec({
    "Given old, parseFrom returns the OldValue object." {
        Operand.parseFrom("old") shouldBe Operand.OldValue
    }

    "Given some integer as String, parseFrom returns a FixedValue." {
        forAll(
            row(1),
            row(0),
            row(-1),
            row(15),
            row(195109)
        ) {
            Operand.parseFrom(it.toString()) shouldBe Operand.FixedValue(it)
        }
    }

    "Given neither old nor an integer, parseFrom throws IllegalArgumentException." {
        forAll(
            row("+"),
            row("new"),
            row(""),
            row("blub"),
            row("1.9")
        ) {
            shouldThrow<IllegalArgumentException> { Operand.parseFrom(it) }
        }
    }

    "Given FixedValue of some integer and some other integer, then evaluate returns the first integer." {
        forAll(
            row(1, 1),
            row(1, 0),
            row(1, -1),
            row(1, 15),
            row(1, 195109),
            row(0, 1),
            row(0, 0),
            row(0, -1),
            row(0, 15),
            row(0, 195109),
            row(-1, 1),
            row(-1, 0),
            row(-1, -1),
            row(-1, 15),
            row(-1, 195109),
            row(15, 1),
            row(15, 0),
            row(15, -1),
            row(15, 15),
            row(15, 195109),
            row(195109, 1),
            row(195109, 0),
            row(195109, -1),
            row(195109, 15),
            row(195109, 195109)
        ) { first, second ->
            Operand.FixedValue(first).evaluate(second) shouldBe first
        }
    }

    "Given OldValue and some integer, then evaluate returns that integer integer." {
        forAll(
            row(1),
            row(0),
            row(-1),
            row(15),
            row(195109)
        ) {
            Operand.OldValue.evaluate(it) shouldBe it
        }
    }
})

class OperatorTest : StringSpec({
    "Given + or *, parseFrom returns the correct Operator." {
        forAll(
            row('+', Operator.PLUS),
            row('*', Operator.TIMES),
            row('/', Operator.DIV)
        ) { char, expectedOperator ->
            Operator.parseFrom(char) shouldBe expectedOperator
        }
    }

    "Given other char, parseFrom throws IllegalArgumentException." {
        forAll(
            row('-'),
            row(' '),
            row('\n'),
            row('%'),
            row('a'),
            row('Z'),
        ) {
            shouldThrow<IllegalArgumentException> { Operator.parseFrom(it) }
        }
    }

    "Given two integers, PLUS adds them." {
        forAll(
            row(0, 1, 1),
            row(1, 0, 1),
            row(-1, 1, 0),
            row(5, 6, 11),
            row(151, -50, 101)
        ) { first, second, expectedResult ->
            with(Operator.PLUS) {
                first op second shouldBe expectedResult
            }
        }
    }

    "Given two integers, TIMES multiplies them." {
        forAll(
            row(0, 1, 0),
            row(1, 0, 0),
            row(-1, 1, -1),
            row(5, 6, 30),
            row(151, -50, -7550)
        ) { first, second, expectedResult ->
            with(Operator.TIMES) {
                first op second shouldBe expectedResult
            }
        }
    }

    "Given two integers, DIV divides them." {
        forAll(
            row(0, 1, 0),
            row(1, 1, 1),
            row(-1, 1, -1),
            row(5, 6, 0),
            row(151, -50, -3),
            row(20, 4, 5),
        ) { first, second, expectedResult ->
            with(Operator.DIV) {
                first op second shouldBe expectedResult
            }
        }
    }
})

class OperationTest : StringSpec({
    "Given two operands, an operator and an integer, the constructed Operator applied to the integer returns the expected result." {
        forAll(
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.PLUS, 0, 4),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.PLUS, 0, 4),
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.PLUS, -1, 4),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.PLUS, -1, 4),
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.PLUS, 100, 4),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.PLUS, 100, 4),
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.TIMES, 0, 3),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.TIMES, 0, 3),
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.TIMES, -1, 3),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.TIMES, -1, 3),
            row(Operand.FixedValue(1), Operand.FixedValue(3), Operator.TIMES, 100, 3),
            row(Operand.FixedValue(3), Operand.FixedValue(1), Operator.TIMES, 100, 3),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.PLUS, 0, 1),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.PLUS, 0, 3),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.PLUS, -1, 0),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.PLUS, -1, 2),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.PLUS, 100, 101),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.PLUS, 100, 103),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.PLUS, 0, 1),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.PLUS, 0, 3),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.PLUS, -1, 0),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.PLUS, -1, 2),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.PLUS, 100, 101),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.PLUS, 100, 103),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.TIMES, 0, 0),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.TIMES, 0, 0),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.TIMES, -1, -1),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.TIMES, -1, -3),
            row(Operand.FixedValue(1), Operand.OldValue, Operator.TIMES, 100, 100),
            row(Operand.FixedValue(3), Operand.OldValue, Operator.TIMES, 100, 300),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.TIMES, 0, 0),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.TIMES, 0, 0),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.TIMES, -1, -1),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.TIMES, -1, -3),
            row(Operand.OldValue, Operand.FixedValue(1), Operator.TIMES, 100, 100),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.TIMES, 100, 300),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.DIV, 3, 1),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.DIV, 6, 2),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.DIV, -3, -1),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.DIV, 5, 1),
            row(Operand.OldValue, Operand.FixedValue(3), Operator.DIV, 125, 41),
        ) { firstOperand, secondOperand, operator, value, expectedResult ->
            Operation(firstOperand, secondOperand, operator).apply(value) shouldBe expectedResult
        }
    }

    "Given an operation as string and an integer, the Operator constructed by parseFrom applied to the integer returns the expected result." {
        forAll(
            row("new = 1 + 3", 0, 4),
            row("new = 3 + 1", 0, 4),
            row("new = 1 + 3", -1, 4),
            row("new = 3 + 1", -1, 4),
            row("new = 1 + 3", 100, 4),
            row("new = 3 + 1", 100, 4),
            row("new = 1 * 3", 0, 3),
            row("new = 3 * 1", 0, 3),
            row("new = 1 * 3", -1, 3),
            row("new = 3 * 1", -1, 3),
            row("new = 1 * 3", 100, 3),
            row("new = 3 * 1", 100, 3),
            row("new = 1 + old", 0, 1),
            row("new = 3 + old", 0, 3),
            row("new = 1 + old", -1, 0),
            row("new = 3 + old", -1, 2),
            row("new = 1 + old", 100, 101),
            row("new = 3 + old", 100, 103),
            row("new = old + 1", 0, 1),
            row("new = old + 3", 0, 3),
            row("new = old + 1", -1, 0),
            row("new = old + 3", -1, 2),
            row("new = old + 1", 100, 101),
            row("new = old + 3", 100, 103),
            row("new = 1 * old", 0, 0),
            row("new = 3 * old", 0, 0),
            row("new = 1 * old", -1, -1),
            row("new = 3 * old", -1, -3),
            row("new = 1 * old", 100, 100),
            row("new = 3 * old", 100, 300),
            row("new = old * 1", 0, 0),
            row("new = old * 3", 0, 0),
            row("new = old * 1", -1, -1),
            row("new = old * 3", -1, -3),
            row("new = old * 1", 100, 100),
            row("new = old * 3", 100, 300)
        ) { operationString, value, expectedResult ->
            Operation.parseFrom(operationString).apply(value) shouldBe expectedResult
        }
    }
})

class MonkeyBusinessTest : StringSpec({
    "Given the example, the calculated monkey business is 10605." {
        val input = """Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1""".split("\n")

        MonkeyBusiness.parseFrom(input).playRounds(20) shouldBe 10605
    }
})