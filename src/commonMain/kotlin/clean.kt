fun clean(args: CompilationArgs) {
    println("--- Cleaning ---")
    SYSTEM.deleteRecursively(args.buildFolder)
    SYSTEM.deleteRecursively(args.outFolder)
    println("--- Finished ---")
}