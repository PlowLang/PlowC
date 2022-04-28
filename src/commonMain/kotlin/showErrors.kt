import com.drjcoding.plow.issues.PlowIssue
import com.drjcoding.plow.issues.PlowIssueTextRange
import com.drjcoding.plow.issues.PlowResult

fun <T> PlowResult<T>.showError(fileCode: String?): T {
    when (this) {
        is PlowResult.Error -> {
            this.issues.forEach { printIssue(it, fileCode) }
            exitProcess(1)
        }
        is PlowResult.Ok -> return this.result
    }
}

fun printIssue(issue: PlowIssue, file: String?) {
    println()
    println("error: ${issue.errorName}")

    if (file != null) {
        val lines = file.split("\n")
        if (issue.mainInfo.textRange != null) {
            val range = (issue.mainInfo.textRange!! as PlowIssueTextRange.SameFile).sourceFileRange

            if (range.start.line == range.end.line) {
                for (i in (range.start.line - 3).coerceAtLeast(0)..(range.end.line + 1).coerceAtMost(lines.size - 1)) {
                    println("$i\t| ${lines[i]}".trim())
                    if (i == range.start.line - 1) {
                        print(" \t  ")
                        var content = false
                        for ((j, c) in lines[i].withIndex()) {
                            if (j in (range.start.col-1) until (range.end.col-1) && (content || c != ' ')) {
                                print("^")
                                content = true
                            } else {
                                print(" ")
                            }
                        }
                        println()
                    }
                }
                println()
            } else {
                println(issue.mainInfo.textRange?.toString()?.plus(": ") ?: "")
            }
        }
    }
    println(issue.mainInfo.message)

    for (note in issue.notes) {
        print("note: ")
        print(note.textRange?.toString()?.plus(": ") ?: "")
        println(note.message)
    }
}
