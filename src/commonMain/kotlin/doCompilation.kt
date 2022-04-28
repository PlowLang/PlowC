import com.drjcoding.plow.issues.runCatchingExceptionsAsPlowResult
import com.drjcoding.plow.project.FolderStructure
import com.drjcoding.plow.project.RawPlowProject
import com.drjcoding.plow.source_abstractions.toSourceString
import okio.Path

private fun simpleFileStructure(file: Path): RawPlowProject {
    return FolderStructure.File(
        "file".toSourceString(),
        SYSTEM.read(file) { readUtf8() }.replace("\t", "    ")
    )
}

private fun writeFiles(args: CompilationArgs, plowLLVM: String, cLike: List<Path>) {
    val srcFolder = args.buildFolder.div("src")
    SYSTEM.createDirectory(args.buildFolder)
    SYSTEM.createDirectory(srcFolder)

    cLike.forEach {
        val writePath = srcFolder.div(it.relativeTo(args.srcFolder))
        val text = SYSTEM.read(it) { readUtf8() }
        SYSTEM.write(writePath) { writeUtf8(text) }
    }

    val plowWritePath = srcFolder.div("plow.ll")
    SYSTEM.write(plowWritePath) { writeUtf8(plowLLVM) }

    val makefile = args.buildFolder.div("makefile")
    SYSTEM.write(makefile) { writeUtf8(makefileText(args.programName)) }
}

private fun tryCopy(args: CompilationArgs): Boolean {
    return try {
        val fileName = "${args.programName}.8xp"
        val expectedOutPath = args.buildFolder.div("bin").div(fileName)
        val file = SYSTEM.read(expectedOutPath) { readByteArray() }
        SYSTEM.createDirectory(args.outFolder)
        SYSTEM.write(args.outFolder.div(fileName)) { write(file) }
        true
    } catch (e: Exception) {
        false
    }
}

fun doCompilation(args: CompilationArgs) =
    runCatchingExceptionsAsPlowResult {
        println("--- Compiling Plow ---")
        val files = readFiles(args.srcFolder)
        val plowLLVM = generateLLVM(simpleFileStructure(files.plow)).unwrapThrowingErrors()
        writeFiles(args, plowLLVM, files.cLike)

        println("--- Compiling C and Linking ---")
        exec("cd target && make")
        val copied = tryCopy(args)

        if (copied) {
            if (args.launch) {
                println("--- Launching ---")
                val fileName = "${args.programName}.8xp"
                val outFile = args.outFolder.div(fileName)
                exec("cemu --send ${SYSTEM.canonicalize(outFile)} --no-reset --launch ${args.programName}")
            }

            println("--- Finished ---")
            exitProcess(0)
        } else {
            println("--- An Error Occurred not Involving Plow Code. See Above ---")
            exitProcess(1)
        }
    }
