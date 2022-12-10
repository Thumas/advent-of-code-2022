package de.tek.adventofcode.y2022.day01

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalorieListTest: FunSpec() {
    init {
        val input = ("1000\n" +
                "2000\n" +
                "3000\n" +
                "\n" +
                "4000\n" +
                "\n" +
                "5000\n" +
                "6000\n" +
                "\n" +
                "7000\n" +
                "8000\n" +
                "9000\n" +
                "\n" +
                "10000").split('\n')

        test("getMaximumCalories") {
            CalorieList(input).getMaximumCalories() shouldBe 24000
        }

        test("getTopThreeCaloriesSum") {
            CalorieList(input).getTopThreeCaloriesSum() shouldBe 45000
        }
    }
}