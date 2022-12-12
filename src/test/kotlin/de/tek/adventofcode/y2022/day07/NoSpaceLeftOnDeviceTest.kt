package de.tek.adventofcode.y2022.day07

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.assertj.core.api.Assertions

class DirectoryTest : StringSpec({
    var root = Directory.newRoot()
    beforeTest {
        root = Directory.newRoot()
    }

    "Given new File, add returns it." {
        val file = File("test", 123)

        root.add(file) shouldBeSameInstanceAs file
    }

    "Given File with same name as already added File, add returns the old instance." {
        val file = File("test", 123)
        root.add(file)
        val fileWithSameName = File("test", 321)

        root.add(fileWithSameName) shouldBeSameInstanceAs file
    }

    "Given child files, getSize returns the sum of their sizes" {
        forAll(
            row(listOf(15, 662, 262, 9762), 10701),
        ) { fileSizes, expectedResult ->
            val d = Directory.newRoot()
            fileSizes.forEachIndexed { index, fileSize ->
                d.add(File(index.toString(), fileSize))
            }

            d.getSize() shouldBe expectedResult
        }
    }

    "Given name, getOrCreateSubdirectory returns directory with that name." {
        val newDirectory = root.getOrCreateSubdirectory("test")

        newDirectory.name shouldBe "test"
    }

    "Given name, getOrCreateSubdirectory returns directory with current directory as parent." {
        val newDirectory = root.getOrCreateSubdirectory("test")

        newDirectory.parent shouldBe root
    }

    "Given child files in subdirectories, getSize returns the sum of their sizes" {
        forAll(
            row(listOf(15, 662, 262, 9762), 10701),
        ) { fileSizes, expectedResult ->
            var directory = root
            fileSizes.forEachIndexed { index, fileSize ->
                directory = directory.getOrCreateSubdirectory(index.toString())
                directory.add(File(index.toString(), fileSize))
            }

            root.getSize() shouldBe expectedResult
        }
    }

    "Given directory tree, iterator iterates as depth-first search." {
        val dirA = root.getOrCreateSubdirectory("a")
        val fileB = File("b.txt", 14848514)
        root.add(fileB)
        val fileC = File("c.dat", 8504156)
        root.add(fileC)
        val dirD = root.getOrCreateSubdirectory("d")
        val dirE = dirA.getOrCreateSubdirectory("e")
        val fileF = File("f", 29116)
        dirA.add(fileF)
        val fileG = File("g", 2557)
        dirA.add(fileG)
        val fileH = File("h.lst", 62596)
        dirA.add(fileH)
        val fileI = File("i", 584)
        dirE.add(fileI)
        val fileJ = File("j", 4060174)
        dirD.add(fileJ)
        val fileDLog = File("d.log", 8033020)
        dirD.add(fileDLog)
        val fileDExt = File("d.ext", 5626152)
        dirD.add(fileDExt)
        val fileK = File("k", 7214296)
        dirD.add(fileK)

        root.toList() shouldBe listOf(
            dirA,
            dirE,
            fileI,
            fileF,
            fileG,
            fileH,
            fileB,
            fileC,
            dirD,
            fileJ,
            fileDLog,
            fileDExt,
            fileK
        )
    }
})


class NoSpaceLeftOnDeviceTest : StringSpec({
    val input = """${'$'} cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} cd e
${'$'} ls
584 i
${'$'} cd ..
${'$'} cd ..
${'$'} cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k""".split("\n")

    val expectedRoot = FileSystem().root
    val dirA = expectedRoot.getOrCreateSubdirectory("a")
    expectedRoot.add(File("b.txt", 14848514))
    expectedRoot.add(File("c.dat", 8504156))
    val dirD = expectedRoot.getOrCreateSubdirectory("d")
    val dirE = dirA.getOrCreateSubdirectory("e")
    dirA.add(File("f", 29116))
    dirA.add(File("g", 2557))
    dirA.add(File("h.lst", 62596))
    dirE.add(File("i", 584))
    dirD.add(File("j", 4060174))
    dirD.add(File("d.log", 8033020))
    dirD.add(File("d.ext", 5626152))
    dirD.add(File("k", 7214296))

    "analyzeLog for example" {
        Assertions.assertThat(analyzeLog(input).root).usingRecursiveComparison().isEqualTo(expectedRoot)
    }

    "file size sum for example" {
        analyzeLog(input).root.getSize() shouldBe 48381165
    }

    "subdirectorySizes for example" {
        expectedRoot.subdirectorySizes().toList() shouldContainAll listOf(584, 94853, 24933642, 48381165)
    }
})