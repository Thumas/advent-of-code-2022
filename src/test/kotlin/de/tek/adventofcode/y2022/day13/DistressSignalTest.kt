package de.tek.adventofcode.y2022.day13

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.math.sign

private fun PacketList.add(vararg ints: Int) = ints.fold(this) { list, int -> list.add(Integer(int)) }

class PacketTest : StringSpec({
    "Given JSON, toPacket returns correct Packet." {
        forAll(
            row("[1,1,3,1,1]", Packet().add(1, 1, 3, 1, 1)),
            row("[1,1,5,1,1]", Packet().add(1, 1, 5, 1, 1)),
            row("[[1],[2,3,4]]", Packet(Packet().add(1), Packet().add(2, 3, 4))),
            row("[[1],4]", Packet(Packet().add(1)).add(4)),
            row("[9]", Packet().add(9)),
            row("[[8,7,6]]", Packet(Packet().add(8, 7, 6))),
            row("[[4,4],4,4]", Packet(Packet().add(4, 4)).add(4, 4)),
            row("[[4,4],4,4,4]", Packet(Packet().add(4, 4)).add(4, 4, 4)),
            row("[]", Packet()),
            row("[[[]]]", Packet(Packet(Packet()))),
            row(
                "[1,[2,[3,[4,[5,6,7]]]],8,9]",
                Packet().add(1)
                    .add(
                        Packet().add(2)
                            .add(
                                Packet().add(3)
                                    .add(
                                        Packet().add(4)
                                            .add(Packet().add(5, 6, 7))
                                    )
                            )
                    )
                    .add(8, 9)
            )
        ) { string, expectedResult ->
            Packet.from(Json.parseToJsonElement(string)) shouldBe expectedResult
        }
    }

    "Given two Packets, then the result of compareTo has the correct sign." {
        forAll(
            row(Packet().add(1, 1, 3, 1, 1), Packet().add(1, 1, 5, 1, 1), -1),
            row(Packet(Packet().add(1), Packet().add(2, 3, 4)), Packet(Packet().add(1)).add(4), -1),
            row(Packet().add(9), Packet(Packet().add(8, 7, 6)), 1),
            row(Packet(Packet().add(4, 4)).add(4, 4), Packet(Packet().add(4, 4)).add(4, 4, 4), -1),
            row(Packet().add(7, 7, 7, 7), Packet().add(7, 7, 7), 1),
            row(Packet(), Packet().add(3), -1),
            row(Packet(Packet(Packet())), Packet(Packet()), 1),
            row(
                Packet().add(1)
                    .add(
                        Packet().add(2)
                            .add(
                                Packet().add(3)
                                    .add(
                                        Packet().add(4)
                                            .add(Packet().add(5, 6, 7))
                                    )
                            )
                    )
                    .add(8, 9),
                Packet().add(1)
                    .add(
                        Packet().add(2)
                            .add(
                                Packet().add(3)
                                    .add(
                                        Packet().add(4)
                                            .add(Packet().add(5, 6, 0))
                                    )
                            )
                    )
                    .add(8, 9), 1
            )
        ) { firstPacket: Packet, secondPacket: Packet, expectedSign: Int ->
            firstPacket.compareTo(secondPacket).sign shouldBe expectedSign
        }
    }
})

class DistressSignalTest : StringSpec({
    "Given the example, the indices of the packet pairs that are in the right order sum to 13." {
        val input = """[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]""".split("\n")

        part1(input) shouldBe 13
    }
})