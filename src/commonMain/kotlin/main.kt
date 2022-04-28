import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import okio.FileSystem
import okio.Path.Companion.toPath

expect val SYSTEM: FileSystem
expect fun exitProcess(code: Int): Nothing
expect fun exec(command: String)

class CLI : CliktCommand() {
    val name: String? by argument().optional()
    val inputFolder: String? by argument().optional()
    val clean by option("--clean").flag(default = false)
    val launch by option("--launch", "-l").flag(default = false)

    override fun run() {
        val inputFolder = (inputFolder ?: ".").toPath()
        val name = (name ?: SYSTEM.canonicalize(inputFolder).name).take(8).uppercase()

        val args = CompilationArgs(
            inputFolder.div("src"),
            inputFolder.div("target"),
            inputFolder.div("bin"),
            name,
            launch
        )

        if (clean) {
            clean(args)
        } else {
            doCompilation(
                args
            ).showError(null)
        }
    }
}

fun main(args: Array<String>) = CLI().main(args)
