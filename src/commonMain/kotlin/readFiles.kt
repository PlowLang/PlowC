import com.drjcoding.plow.issues.PlowError
import com.drjcoding.plow.issues.PlowIssueInfo
import okio.Path

class NoPlowFileError : PlowError(
    "no plow file",
    PlowIssueInfo(null, "Could not find a plow file to compile.")
)

class TooManyPlowFilesError : PlowError(
    "too many plow files",
    PlowIssueInfo(null, "Compiling multiple plow files together is not yet supported.")
)

data class CompilationFiles(
    val base: Path,
    val plow: List<Path>,
    val cLike: List<Path>
)

fun readFiles(file: Path): CompilationFiles {
    val cLike = mutableListOf<Path>()
    val plow = mutableListOf<Path>()

    try {
        SYSTEM
            .listRecursively(file)
            .forEach {
                if (it.name.endsWith(".c") || it.name.endsWith(".ll")) {
                    cLike.add(it)
                } else if (it.name.endsWith(".plow")) {
                    plow.add(it)
                }
            }
    } catch (e: Exception) {
    }

    return CompilationFiles(file, plow, cLike)
}