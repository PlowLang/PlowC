import okio.FileSystem
import kotlin.system.exitProcess

actual val SYSTEM = FileSystem.SYSTEM

actual fun exitProcess(code: Int): Nothing {
    exitProcess(code)
}

actual fun exec(command: String) {
    Runtime.getRuntime().exec(command)
}