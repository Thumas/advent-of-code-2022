package de.tek.adventofcode.y2022.day11

import de.tek.adventofcode.y2022.util.readInputLines
import java.util.concurrent.atomic.AtomicInteger

open class Item(initialWorryLevel: Int) {
    private var worryLevel = initialWorryLevel
        private set

    fun applyOperation(operation: Operation) {
        worryLevel = operation.apply(worryLevel)
    }

    open fun reduceWorry() {
        worryLevel /= 3
    }

    fun test(divisor: Int) = worryLevel.mod(divisor) == 0

    override fun toString(): String {
        return "Item(worryLevel=$worryLevel)"
    }
}

class FragileItem(initialWorryLevel: Int) : Item(initialWorryLevel) {
    override fun reduceWorry() {}
}

class Operation(firstOperand: Operand, secondOperand: Operand, private val operator: Operator) {
    private val operands = listOf(firstOperand, secondOperand)
    fun apply(oldValue: Int): Int {
        val toTypedArray = operands.map { it.evaluate(oldValue) }.toTypedArray()
        with(operator) {
            return toTypedArray[0] op toTypedArray[1]
        }
    }

    companion object {
        fun parseFrom(string: String): Operation {
            val splitString = string.split(" ")
            val firstOperand = Operand.parseFrom(splitString[2])
            val operator = Operator.parseFrom(splitString[3][0])
            val secondOperand = Operand.parseFrom(splitString[4])

            return Operation(firstOperand, secondOperand, operator)
        }
    }
}

sealed class Operand {
    class FixedValue(private val value: Int) : Operand() {
        override fun evaluate(value: Int) = this.value

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is FixedValue) return false

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value
        }
    }

    object OldValue : Operand() {
        override fun evaluate(value: Int) = value
    }

    abstract fun evaluate(value: Int): Int

    companion object {
        fun parseFrom(string: String) = when (string) {
            "old" -> OldValue
            else -> {
                val value = string.toIntOrNull() ?: throw IllegalArgumentException("Cannot parse operand $string.")
                FixedValue(value)
            }
        }
    }
}

enum class Operator(private val char: Char, private val biFunction: (Int, Int) -> Int) {
    PLUS('+', Int::plus), TIMES('*', Int::times), DIV('/', Int::div);

    infix fun Int.op(other: Int): Int = biFunction(this, other)

    companion object {
        fun parseFrom(char: Char): Operator {
            return Operator.values().find { char == it.char }
                ?: throw IllegalArgumentException("Unknown operator $char.")
        }
    }
}

class Monkey(
    private val name: String,
    initialItems: List<Item>,
    private val operation: Operation,
    private val divisor: Int
) {
    private val currentItems = initialItems.toMutableList()
    private lateinit var firstMonkeyFriend: Monkey
    private lateinit var secondMonkeyFriend: Monkey

    private val inspectionCounter = AtomicInteger()

    fun makeFriends(firstMonkey: Monkey, secondMonkey: Monkey) {
        if (firstMonkey === this || secondMonkey === this) {
            throw IllegalArgumentException("A monkey cannot be friends with itself!")
        }

        firstMonkeyFriend = firstMonkey
        secondMonkeyFriend = secondMonkey
    }

    fun handleItems() {
        while (currentItems.isNotEmpty()) {
            val item = currentItems.removeFirst()
            handleItem(item)
        }
    }

    private fun handleItem(item: Item) {
        inspect(item)
        getBoredWith(item)

        val nextMonkey = determineNextMonkey(item)

        item throwTo nextMonkey
    }

    private fun inspect(item: Item) {
        inspectionCounter.incrementAndGet()
        item.applyOperation(operation)
    }

    private fun getBoredWith(item: Item) {
        item.reduceWorry()
    }

    private fun determineNextMonkey(item: Item) = if (item.test(divisor)) firstMonkeyFriend else secondMonkeyFriend

    private infix fun Item.throwTo(friend: Monkey) = friend.catch(this)

    private fun catch(item: Item) = currentItems.add(item)

    fun getInspectionCount(): Int = inspectionCounter.get()
    override fun toString(): String {
        return "Monkey(name=$name, operation=$operation, divisor=$divisor, currentItems=$currentItems, firstMonkeyFriend=${firstMonkeyFriend.name}, secondMonkeyFriend=${secondMonkeyFriend.name}, inspectionCounter=$inspectionCounter)"
    }


}

class MonkeyBusiness private constructor(private val monkeys: Map<Int, Monkey>) {

    fun playRounds(rounds: Int): Int {
        repeat(rounds) {
            for (monkey in monkeys.values) {
                monkey.handleItems()

            }
            println("After round $it: ${monkeys.values}")
        }

        return monkeys.values.map { it.getInspectionCount() }.sorted().takeLast(2).reduce(Int::times)
    }

    companion object {

        var worryReductionEnabled = true

        private val regexPerLine = arrayOf(
            Regex("""Monkey (\d+):"""),
            Regex("""\s*Starting items: (.*)"""),
            Regex("""\s*Operation: (.*)"""),
            Regex("""\s*Test: divisible by (\d+)"""),
            Regex("""\s*If true: throw to monkey (\d+)"""),
            Regex("""\s*If false: throw to monkey (\d+)""")
        )

        fun parseFrom(input: List<String>): MonkeyBusiness {
            val monkeyDescriptions = splitIntoMonkeyDescriptions(input).map { parseLines(it) }

            val splitDescriptions = monkeyDescriptions.associate(::splitMonkeyDescription).toPairOfMaps()
            val monkeys = splitDescriptions.first.mapValues { parseMonkey(it.key, it.value) }
            associateFriends(splitDescriptions.second, monkeys)

            return MonkeyBusiness(monkeys)
        }

        private fun splitIntoMonkeyDescriptions(input: List<String>): MutableList<MutableList<String>> {
            val monkeyDescriptions = mutableListOf(mutableListOf<String>())
            for (line in input) {
                if (line.isBlank()) {
                    monkeyDescriptions.add(mutableListOf())
                } else {
                    monkeyDescriptions.last().add(line)
                }
            }
            return monkeyDescriptions
        }

        private fun parseLines(monkeyDescription: List<String>): List<String> =
            regexPerLine.zip(monkeyDescription) { regex, line ->
                val match = regex.matchEntire(line)
                match?.destructured?.component1()
                    ?: throw IllegalArgumentException("Cannot parse line of monkey description against \"$regex\": $line")
            }

        private fun splitMonkeyDescription(parsedLines: List<String>): Pair<Int, Pair<List<String>, List<String>>> {
            val monkeyNumber = parsedLines[0].toInt()

            val monkeySelfDescription = parsedLines.subList(0, 4)
            val monkeyFriendDescription = parsedLines.subList(4, 6)

            return Pair(monkeyNumber, Pair(monkeySelfDescription, monkeyFriendDescription))
        }

        private fun <K, V, W> Map<K, Pair<V, W>>.toPairOfMaps(): Pair<Map<K, V>, Map<K, W>> {
            val result = Pair(mutableMapOf<K, V>(), mutableMapOf<K, W>())
            for (entry in this.entries) {
                result.first[entry.key] = entry.value.first
                result.second[entry.key] = entry.value.second
            }
            return result
        }

        private fun parseMonkey(number: Int, parseResults: List<String>): Monkey {
            val items = parseItems(parseResults[1])
            val operation = Operation.parseFrom(parseResults[2])
            val divisor = parseResults[3].toInt()

            return Monkey(number.toString(), items, operation, divisor)
        }

        private fun parseItems(itemString: String): List<Item> =
            itemString.split(",").map(String::trim).map(String::toInt).map(::newItem)

        private fun newItem(worry: Int) = if (worryReductionEnabled) Item(worry) else FragileItem(worry)

        private fun associateFriends(friendDescriptions: Map<Int, List<String>>, monkeys: Map<Int, Monkey>) {
            friendDescriptions
                .mapValues { parseFriends(it.value, monkeys) }
                .mapKeys { monkeys[it.key]!! }
                .forEach { monkeyAndFriends ->
                    makeFriends(monkeyAndFriends)
                }
        }

        private fun parseFriends(friendDescription: List<String>, monkeys: Map<Int, Monkey>) =
            parseFriendNumbers(friendDescription).map { slot ->
                monkeys[slot] ?: throw IllegalArgumentException("There is no monkey ${slot}.")
            }

        private fun parseFriendNumbers(friendDescription: List<String>): Pair<Int, Int> {
            val firstMonkeyFriendNumber = friendDescription[0].toInt()
            val secondMonkeyFriendNumber = friendDescription[1].toInt()

            return Pair(firstMonkeyFriendNumber, secondMonkeyFriendNumber)
        }

        private fun <T, S> Pair<T, T>.map(transform: (T) -> S): Pair<S, S> = Pair(transform(first), transform(second))

        private fun makeFriends(monkeyAndFriends: Map.Entry<Monkey, Pair<Monkey, Monkey>>) {
            val thisMonkey = monkeyAndFriends.key
            val firstFriend = monkeyAndFriends.value.first
            val secondMonkey = monkeyAndFriends.value.second

            thisMonkey.makeFriends(firstFriend, secondMonkey)
        }
    }
}

fun main() {
    val input = readInputLines(MonkeyBusiness::class)

    fun part1(input: List<String>): Int {
        MonkeyBusiness.worryReductionEnabled = true
        return MonkeyBusiness.parseFrom(input).playRounds(20)
    }

    fun part2(input: List<String>): Int {
        MonkeyBusiness.worryReductionEnabled = false
        return MonkeyBusiness.parseFrom(input).playRounds(10000)
    }

    println("The level of monkey business after 20 rounds of stuff-slinging simian shenanigans is ${part1(input)}.")
    println(
        "Without worry reduction, " +
                "the level of monkey business after 10000 rounds of stuff-slinging simian shenanigans is ${part2(input)}."
    )
}