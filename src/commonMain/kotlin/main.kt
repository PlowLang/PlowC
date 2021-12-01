import com.drjcoding.plow.issues.PlowIssue
import com.drjcoding.plow.issues.PlowIssueTextRange
import com.drjcoding.plow.issues.PlowResult
import com.drjcoding.plow.plow_project.PlowProject
import com.drjcoding.plow.plow_project.PlowProjectSourceFile
import com.drjcoding.plow.plow_project.SimpleFileChild
import com.drjcoding.plow.plow_project.SimpleFileStructure
import com.drjcoding.plow.source_abstractions.toSourceString
import okio.BufferedSource
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path.Companion.toPath

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("enter a file path")
        exitProcess(1)
        return
    }
    val fileName = args.first()
    val fileCode = readFile(fileName) ?: run {
        println("enter a plow file path")
        exitProcess(1)
        return
    }

    val project = PlowProject(
        SimpleFileStructure(
            listOf(SimpleFileChild.SimpleFile(PlowProjectSourceFile(fileName.toPath().name.toSourceString(), fileCode)))
        )
    )
    val lexed = project.toLexedPlowProject()
    if (lexed is PlowResult.Error) {
        lexed.issues.forEach { printIssue(it, fileCode) }
        exitProcess(1)
        return
    }
    val parsed = lexed.unwrap().toParsedPlowProject()
    if (parsed is PlowResult.Error) {
        parsed.issues.forEach { printIssue(it, fileCode) }
        exitProcess(1)
        return
    }
    println("File parsed successfully")
    exitProcess(0)
}

expect val SYSTEM: FileSystem
expect fun exitProcess(code: Int)

fun printIssue(issue: PlowIssue, file: String) {
    println("error: ${issue.errorName}")

    val lines = file.split("\n")
    val range = (issue.mainInfo.textRange!! as PlowIssueTextRange.SameFile).sourceFileRange

    if (range.start.line == range.end.line) {
        for (i in (range.start.line - 3).coerceAtLeast(0)..(range.end.line + 1).coerceAtMost(lines.size - 1)) {
            println("$i\t| ${lines[i]}".trim())
            if (i == range.start.line - 1) {
                println(" \t  " + " ".repeat(range.start.col - 1) + "^".repeat(range.end.col - range.start.col))
            }
        }
        println()
    } else {
        println(issue.mainInfo.textRange?.toString()?.plus(": ") ?: "")
    }
    println(issue.mainInfo.message)

    for (note in issue.notes) {
        print("note: ")
        print(note.textRange?.toString()?.plus(": ") ?: "")
        println(note.message)
    }
}

internal fun readFile(file: String): String? {
    return try {
        val filePath = file.toPath()
        SYSTEM.read(filePath, BufferedSource::readUtf8)
    } catch (e: FileNotFoundException) {
        null
    }
}

