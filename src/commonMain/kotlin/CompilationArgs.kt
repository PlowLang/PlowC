import okio.Path

data class CompilationArgs(
    val srcFolder: Path,
    val buildFolder: Path,
    val outFolder: Path,
    val programName: String,
    val launch: Boolean
)