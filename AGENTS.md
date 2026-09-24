# Working in this repository

- This is the Windows Java 8 client for the matching `2006sp-Server`. Use JDK 1.8.0_101 or another compatible JDK 8; `java`, `javac`, and `jar` must be on `PATH`.
- Start with `docs/source-map.md` to locate code. The Java source is under `src/main/java`; smoke tests are under `src/test/java`.
- Search source first: `rg -n "pattern" src/main/java` or `rg --files src/main/java`. The tracked game cache has over 48,000 files. `.ignore` keeps it out of ordinary ripgrep searches; pass a specific cache path when asset work requires it.
- Run `check.bat` after Java changes. It compiles every source file and runs server-free smoke tests. Run `build.bat` to package `build/Client.jar` when a runnable artifact is needed. `build.bat --check` runs just the compiler check and returns a failing exit code on errors.
- The client runs from `runtime/` via `run.bat`, which needs the matching server for gameplay. Keep generated files in `build/` and do not commit local `runtime/userConfig.cfg` settings or login details.
- Preserve Java 8 language and library compatibility. The source is recovered/decompiled, so edit narrowly and check callers before changing method signatures.
