# 2006Scape Singleplayer Client

This is the desktop game client for the 2006Scape single-player project. It is a
Java client for the matching `2006sp-Server`, with the game cache, interfaces,
audio, world map, and client configuration included in this repository.

The default server entry is `127.0.0.1`, so the client connects to a server
running on the same computer. The normal game port is `43594`.

## Requirements

- Windows (the included build and run scripts are batch files)
- JDK 1.8.0_101 (Java SE Development Kit 8u101) - [Oracle Java SE 8 Archive Downloads](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
- `java`, `javac`, and `jar` available on `PATH`
- The matching `2006sp-Server` project if you want to play locally

The normal client/runtime files are kept in the repository. The first GPU-enabled
build also downloads LWJGL 2.9.3 and its Windows native library bundle directly
from Maven Central; later builds reuse the downloaded copies.

- `lib/theme.jar` - compile-time theme library
- `lib/client-runtime.jar` - launcher and packaging base
- `lib/lwjgl-2.9.3.jar` - downloaded OpenGL binding used by the GPU rasterizer
- `runtime/natives/` - extracted LWJGL Windows native libraries
- `runtime/cache/` - game cache and runtime assets

JDK 8 is recommended because the source uses the legacy `sun.audio.AudioPlayer`
API. A Java Runtime Environment (JRE) alone can run an existing build, but it
cannot compile the source.

## Build

From File Explorer, double-click `build.bat`. From Command Prompt, run:

```bat
cd /d "C:\Users\Callum\Downloads\New folder\2006sp client"
build.bat
```

The script compiles every Java file under `src/main/java` and creates:

```text
build/Client.jar
```

Compiler diagnostics are written under `build/` if a source file fails to
compile.

## Run

1. Build and start the matching server.
2. In the server control panel, click **Start Server**.
3. Run the client:

```bat
cd /d "C:\Users\Callum\Downloads\New folder\2006sp client"
run.bat
```

`run.bat` starts the client from the `runtime` directory so it can locate the
cache and `userConfig.cfg`. It also prepares the tiled control-panel world map
before launching `build/Client.jar`.

If the JAR is missing, run `build.bat` first. If the client cannot connect,
confirm that the server is online and listening on port `43594`.

## Renderer modes

The client now defaults to **GPU Rendering**. The `Renderer` menu can switch at
runtime between the OpenGL triangle rasterizer and the original software
`Rasterizer3D` implementation. The same preference is available as
`[GPU_RENDERING];1` (GPU) or `[GPU_RENDERING];0` (software) in `userConfig.cfg`.
If OpenGL or its native library cannot initialize, triangle calls automatically
fall back to the software rasterizer for compatibility.

The GPU path keeps the existing scene/model callers and moves flat, Gouraud,
textured, and depth triangle coverage/interpolation into an off-screen OpenGL
buffer. The main scene stays on the GPU for the full `renderScene` pass and is
read back once before fog/overlays; standalone triangle calls outside that pass
still use a small bounding-box readback. If a legacy-only triangle is encountered
mid-frame, accumulated GPU output is copied back once and the rest of that frame
continues in software.

GPU staging memory deliberately uses resize hysteresis. Small window-size changes
keep existing direct buffers to avoid allocation churn, but a buffer or fallback
Pbuffer is downsized once its capacity is at least four times the current
requirement. Temporary transition-frame storage is released after the handoff,
and renderer disable/context loss drops reusable CPU staging buffers. As a
result, native/heap usage can remain somewhat above the exact current frame size
during normal resizing, but a temporary 4K/fullscreen resize is not retained as
the permanent staging-memory high-water mark.

## Configuration

Client preferences are stored in `runtime/userConfig.cfg`. This includes display
options, renderer selection, loot highlighting, XP drops, key bindings, audio,
and optional saved login details. Close the client before changing settings
marked that way in the file.

## Project layout

```text
src/main/java/       Client, renderer, networking, audio, UI, and world-map source
lib/                 Bundled dependencies plus downloaded LWJGL jar
runtime/cache/       Required game cache and assets
runtime/userConfig.cfg
build.bat            Compiles and packages the client
run.bat              Launches the built client
build/Client.jar     Generated executable JAR
```
