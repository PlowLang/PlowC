import okio.FileSystem
import kotlin.system.exitProcess

actual val SYSTEM = FileSystem.SYSTEM

actual fun exitProcess(code: Int) {
    exitProcess(code)
}