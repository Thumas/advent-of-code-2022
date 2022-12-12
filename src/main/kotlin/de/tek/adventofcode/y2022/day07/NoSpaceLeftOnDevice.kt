package de.tek.adventofcode.y2022.day07

import de.tek.adventofcode.y2022.util.readInputLines

abstract class FileSystemElement(val name: String) {
    abstract fun getSize(): Int
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileSystemElement) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

class File(name: String, private val size: Int) : FileSystemElement(name) {
    override fun getSize() = size
    override fun toString(): String {
        return "File(name=$name, size=$size)"
    }
}

class Directory private constructor(name: String, val parent: Directory?) : FileSystemElement(name),
    Iterable<FileSystemElement> {
    companion object {
        fun newRoot() = Directory("/", null)
    }

    private val children = mutableMapOf<FileSystemElement, FileSystemElement>()
    override fun getSize(): Int = children.keys.sumOf { it.getSize() }

    fun getOrCreateSubdirectory(name: String): Directory {
        val newSubdirectory = Directory(name, this)
        return this.getOrCreateChild(newSubdirectory)
    }

    private fun <T : FileSystemElement> getOrCreateChild(child: T): T {
        return if (children.containsKey(child)) {
            children[child]!! as T
        } else {
            children[child] = child
            child
        }
    }

    fun add(file: File): File = getOrCreateChild(file)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Directory) return false
        if (!super.equals(other)) return false

        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val depth = getDepth()

        val parentDescription = if (parent != null) " parent=${parent.name}," else ""
        return "Directory(name=$name,${parentDescription} children=\n" +
                "${"\t".repeat(depth + 1)}${children.keys}\n${"\t".repeat(depth)})"
    }

    private fun getDepth() = generateSequence(this) { it.parent }.count() - 1

    override fun iterator(): Iterator<FileSystemElement> = object : Iterator<FileSystemElement> {
        private val unvisitedDescendants = mutableListOf(this@Directory as FileSystemElement)

        override fun hasNext() = unvisitedDescendants.isNotEmpty()

        override fun next(): FileSystemElement {
            val next = unvisitedDescendants.removeFirst()
            if (next is Directory) {
                unvisitedDescendants.addAll(0, next.children.keys)
            }
            return next
        }
    }
}

class FileSystem {
    val root = Directory.newRoot()

    private var currentDirectory = root
    fun apply(command: Command) {
        when (command.name) {
            "cd" -> currentDirectory = when (command.parameter) {
                ".." -> currentDirectory.parent
                    ?: throw IllegalArgumentException("Root has no parent, cannot change to top level directory!")

                "/" -> root
                else -> currentDirectory.getOrCreateSubdirectory(command.parameter)
            }

            "ls" -> {
                ListCommand(command.getResults()).apply(currentDirectory)
            }

            else -> throw NotImplementedError("Unknown command ${command}.")
        }
    }
}

class Command(val name: String, val parameter: String) {
    private val results = mutableListOf<String>()
    fun getResults(): List<String> = results.toList()
    fun addResult(result: String) = results.add(result)
    override fun toString(): String {
        return "Command(name='$name', parameter='$parameter', results=${results.joinToString("\n")})"
    }
}

class ListCommand(private val results: List<String>) {
    fun apply(currentDirectory: Directory) {
        val fileResultSyntax = """(\d+) (\S+)""".toRegex()
        for (result in results) {
            if (result.startsWith("dir")) {
                val matchDirectoryResultSyntax = """dir (\S+)""".toRegex().matchEntire(result)
                if (matchDirectoryResultSyntax != null) {
                    val (name) = matchDirectoryResultSyntax.destructured
                    currentDirectory.getOrCreateSubdirectory(name)
                }
            } else if (result.matches(fileResultSyntax)) {
                val matchFileResultSyntax = fileResultSyntax.matchEntire(result)
                if (matchFileResultSyntax != null) {
                    val (size, name) = matchFileResultSyntax.destructured
                    currentDirectory.add(File(name, size.toInt()))
                }
            } else {
                throw IllegalArgumentException("Unknown result of list command $result.")
            }
        }
    }
}

fun analyzeLog(input: List<String>): FileSystem {
    val fileSystem = FileSystem()

    var lastCommand: Command? = null
    for (line in input) {
        if (line.startsWith("$")) {
            if (lastCommand != null) {
                fileSystem.apply(lastCommand)
            }

            val matchCommandSyntax = """^\$\s+([a-z]+)\s*(\S*?)$""".toRegex().matchEntire(line)
            if (matchCommandSyntax != null) {
                val (name, parameter) = matchCommandSyntax.destructured
                lastCommand = Command(name, parameter)
            }
        } else {
            if (lastCommand != null) {
                lastCommand.addResult(line)
            } else {
                throw IllegalArgumentException("First line must be a command!")
            }
        }
    }

    if (lastCommand != null) {
        fileSystem.apply(lastCommand)
    }

    return fileSystem
}

fun Directory.subdirectorySizes(): Sequence<Int> =
    this.iterator().asSequence().filter { it is Directory }.map { it.getSize() }

fun main() {
    val input = readInputLines(FileSystem::class)

    fun part1(input: List<String>) =
        analyzeLog(input).root.subdirectorySizes().filter { it <= 100000 }.sum()

    fun part2(input: List<String>) {
        val diskSize = 70000000
        val requiredUnusedSpace = 30000000

        val root = analyzeLog(input).root

        val currentUnusedSpace = diskSize - root.getSize()
        val spaceToCleanUp = requiredUnusedSpace - currentUnusedSpace

        val result = root.subdirectorySizes().filter { it >= spaceToCleanUp }.min()

        println("The update needs $requiredUnusedSpace. Currently, $currentUnusedSpace is unused. The smallest directory to delete has size $result.")
    }

    println("The sum of all directory sizes individually smaller than 100,000 is ${part1(input)}.")
    part2(input)
}