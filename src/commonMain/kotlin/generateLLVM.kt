import com.drjcoding.plow.issues.PlowResult
import com.drjcoding.plow.issues.runCatchingExceptionsAsPlowResult
import com.drjcoding.plow.issues.toPlowResult
import com.drjcoding.plow.project.RawPlowProject
import com.drjcoding.plow.project.toAST
import com.drjcoding.plow.project.toCST
import com.drjcoding.plow.project.toLexedProject

fun generateLLVM(project: RawPlowProject): PlowResult<String> {
    var sourceCode: String = ""
    project.forEachWithName { s, _, _ ->  sourceCode = s}

    val value = runCatchingExceptionsAsPlowResult {
        val lexed = project.toLexedProject().unwrapThrowingErrors()
        val cst = lexed.toCST().unwrapThrowingErrors()
        val ast = cst.toAST().unwrapThrowingErrors()
        val ir = ast.toIR().unwrapThrowingErrors()
        val llvm = ir.toLLVM()
        llvm.toIRCode()
    }
    when (value) {
        is PlowResult.Ok -> return value.result.toPlowResult()
        is PlowResult.Error -> {
            value.showError(sourceCode)
            exitProcess(1)
        }
    }
}